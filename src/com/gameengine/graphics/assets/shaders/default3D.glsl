#type vertex
#version 330 core

layout(location=0) in vec3 position;

out vec3 color;

uniform mat4 transMatrix;
uniform mat4 projectionMatrix;

void main()
{
	gl_Position = projectionMatrix * transMatrix * vec4(position, 1.0);
	color = vec3(position.x + 0.5, 1.0, position.y + 0.5);
}

#type fragment
#version 330 core

in vec3 color;

out vec4 outColor;

void main()
{
	outColor = vec4(color, 1.0);
}