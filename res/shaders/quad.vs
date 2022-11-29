#version 330 core

const vec2[4] vertices = vec2[4](
    vec2(0, 0),
    vec2(0, 1),
    vec2(1, 1),
    vec2(1, 0)
);

uniform vec4 u_transform;
uniform mat4 u_camera;
uniform vec4 u_uv;

out vec2 v_uv;

void main()
{
    vec2 vertex = vertices[gl_VertexID];
    v_uv = u_uv.zw*vertex + u_uv.xy;
	vec2 world = vertex*u_transform.zw + u_transform.xy;
	vec4 screen = u_camera * vec4(world, 0., 1.);
    gl_Position = screen;
}