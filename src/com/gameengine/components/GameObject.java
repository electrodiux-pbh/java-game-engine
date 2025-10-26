package com.gameengine.components;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.gameengine.Engine;
import com.gameengine.enginegui.GUI;
import com.gameengine.enginegui.GUIable;
import com.gameengine.enginegui.Inspectable;
import com.gameengine.event.EventPerformance;
import com.gameengine.event.components.GameObjectEvent;
import com.gameengine.event.components.GameObjectEventListener;
import com.gameengine.util.Nameable;
import com.gameengine.util.Transform;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class GameObject implements GUIable, Inspectable, Nameable, EventPerformance<GameObjectEventListener, GameObjectEvent> {

	private static int ID_COUNTER = 0;
	
	private String name;
	private int uid = -1;
	public Transform transform;
	private List<Component> components;
	private List<GameObject> childs;
	private transient List<GameObjectEventListener> listeners;
	private transient GameObject parent = null;
	private transient boolean isDead = false;
	
	public GameObject() {
		this(ID_COUNTER++);
	}
	
	public GameObject(@NotNull String name) {
		this(name, new Transform());
	}
	
	public GameObject(Transform transform) {
		this(ID_COUNTER++);
		this.transform = transform;
	}
	
	public GameObject(@NotNull String name, Transform transform) {
		this(name, transform, ID_COUNTER++);
	}
	
	private GameObject(@NotNull String name, @NotNull Transform transform, @NotNull int uid) {
		this.name = name;
		this.uid = uid;
		this.components = new ArrayList<>();
		this.childs = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.transform = transform;
	}
	
	private GameObject(@NotNull int uid) {
		this("Object", new Transform(), uid);
	}
	
	public void engineUpdate() {
		for(Component component : components) {
			component.engineUpdate();
		}
		for(GameObject child : childs) {
			child.engineUpdate();
		}
	}
	
	public void update() {
		for(Component component : components) {
			component.update();
		}
		for(GameObject child : childs) {
			child.update();
		}
	}
	
	public void load() {
		for(Component component : components) {
			component.load();
		}
		for(GameObject child : childs) {
			child.load();
		}
	}
	
	public void destroy() {
		this.isDead = true;
		for(int i = 0; i < components.size(); i++) {
			components.get(i).destroy();
		}
	}
	
	public <C extends Component> C getComponent(@NotNull Class<C> componentClass) {
		for(Component component : components) {
			if(componentClass.isAssignableFrom(component.getClass())) {
				try {
					return componentClass.cast(component);
				} catch (ClassCastException e) {
					e.printStackTrace();
					assert false : "Error casting component";
				}
			}
		}
		return null;
	}
	
	public List<Component> getAllComponents() {
		return components;
	}
	
	public List<GameObject> getChilds() {
		return childs;
	}
	
	public <C extends Component> void removeComponent(@NotNull Class<C> componentClass) {
		Iterator<Component> iter = components.iterator();
		while(iter.hasNext()) {
			Component component = iter.next();
			if(componentClass.isAssignableFrom(component.getClass())) {
				iter.remove();
				performanceEvent(new GameObjectEvent(this, GameObjectEvent.REMOVE_COMPONENT,component));
				return;
			}
		}
	}
	
	public void removeChild(@NotNull GameObject childObj) {
		childs.remove(childObj);
	}
	
	public void addComponent(@NotNull Component component) {
		if(getComponent(component.getClass()) != null)
			return;
		component.generateUID();
		components.add(component);
		component.setParent(this);
		component.load();
		performanceEvent(new GameObjectEvent(this, GameObjectEvent.ADD_COMPONENT,component));
	}
	
	/**
	 * 
	 * @param childObj
	 * @throws java.lang.IllegalStateException
	 */
	public void addChild(@NotNull GameObject childObj) {
		if(childObj == this)
			throw new IllegalStateException("You can't add a game object to it self");
		childs.add(childObj);
		childObj.parent = this;
	}
	
	public void gui() {
		if(ImGui.beginPopupContextWindow("ComponentAdder")) {
			inspectorComponentAdder(SpriteRenderer.class, this);
			inspectorComponentAdder(Rigidbody2D.class, this);
			inspectorComponentAdder(BoxCollider2D.class, this);
			inspectorComponentAdder(CircleCollider.class, this);
			inspectorComponentAdder(Scriptable.class, this);
			ImGui.endPopup();
		}
		
		name = GUI.inputText("Name: ", name);
		if(ImGui.collapsingHeader("Transform", ImGuiTreeNodeFlags.DefaultOpen)) {
			GUI.showTransform(transform);
		}
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			ImGui.pushID(i);
			boolean opened = ImGui.collapsingHeader(component.getClass().getSimpleName());
			if(ImGui.beginPopupContextItem("Component Options")) {
				if(ImGui.menuItem("Remove")) {
					component.destroy();
					components.remove(i);
					i--;
				}
				ImGui.endPopup();
			}
			if(opened) {
				component.gui();
			}
			ImGui.popID();
		}
	}
	
	private static <C extends Component> void inspectorComponentAdder(@NotNull Class<C> clazz, @NotNull GameObject obj) {
		if(ImGui.menuItem(Engine.getEngineLanguage().get("general.add") + " " + clazz.getSimpleName())) {
			if(obj.getComponent(clazz) == null) {
				try {
					obj.addComponent(clazz.getDeclaredConstructor().newInstance());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) { }
			}
		}
	}
	
	private void performanceEvent(@NotNull GameObjectEvent e) {
		for(GameObjectEventListener listener : listeners) {
			listener.execute(e);
		}
	}

	@Override
	public void addEventListener(GameObjectEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeEventListener(GameObjectEventListener listener) {
		listeners.remove(listener);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(@NotNull String name) {
		this.name = name;
	}
	
	public int getUID() {
		return uid;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public GameObject parent() {
		return parent;
	}
	
}
