package com.gameengine.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import com.gameengine.graphics.Model;
import com.gameengine.graphics.RawModel;
import com.gameengine.util.Transform;

import imgui.ImGui;

public class ModelRenderer extends Component {

	private Vector4f color;
	private Model model;
	private transient Transform lastTransform;
	private transient boolean isDirty = false;
	
	public ModelRenderer() {
		this(null);
	}
	
	public ModelRenderer(@NotNull Model model, @NotNull Vector4f color) {
		this.model = model;
		this.color = color;
		isDirty = true;
	}
	
	public ModelRenderer(@NotNull Model model) {
		this(model, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
	}
	
	@Override
	public void load() {
		lastTransform = Transform.clone(parent().transform);
	}
	
	@Override
	public void engineUpdate() {
		if(!lastTransform.equals(parent().transform)) {
			parent().transform.copyTo(lastTransform);
			isDirty = true;
		}
	}
	
	@Override
	public void update() {
		if(!lastTransform.equals(parent().transform)) {
			parent().transform.copyTo(lastTransform);
			isDirty = true;
		}
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public void setModel(@NotNull Model model) {
		this.model = model;
		isDirty = true;
	}
	
	public Model getModel() {
		return model;
	}
	
	public RawModel getRawModel() {
		return model.getRawModel();
	}

	public void setColor(@NotNull Vector4f color) {
		if(!this.color.equals(color)) {
			isDirty = true;
			this.color.set(color);
		}
	}
	
	public void clean() {
		isDirty = false;
	}

	public void setDirty() {
		isDirty = true;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	@Override
	public void componentGui() {
		float[] colorPick = { color.x, color.y, color.z, color.w };
		
		if(ImGui.colorPicker4("Color Picker: ", colorPick)) {
			color.set(colorPick);
			isDirty = true;
		}
	}
	
}
