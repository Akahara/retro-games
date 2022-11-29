#version 330 core

out vec4 color;

in vec2 v_uv;

uniform vec4 u_color;
uniform sampler2D u_texture;

void main()
{
	vec4 c = texture(u_texture, v_uv);
	c = c * u_color;
    color = c;
}
