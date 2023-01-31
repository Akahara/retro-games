#version 330 core

uniform vec2 u_resolution;

uniform float u_CRTStrength = 10;
uniform float u_jaggedStrength = 5;
uniform float u_cornerStretch = .03;

uniform sampler2D u_originTexture;

in vec2 v_uv;

layout(location=0) out vec4 color;

vec4 sampleOriginalTexture(vec2 uv) {
	return abs(uv.x-.5)<.5 && abs(uv.y-.5)<.5 ? texture(u_originTexture, uv) : vec4(0);
}

void main(void) {
  vec2 uv = v_uv*2-1;
  // corner stretch effect
  uv *= 1+u_cornerStretch*length(uv);
  // horizontal jagged effect
  uv.x += length(uv) * u_jaggedStrength/u_resolution.x * (fract(gl_FragCoord.y/2)*2-1);
  // crt effect
  vec2 offset = u_CRTStrength * (.6+.4*length(uv)) * vec2(1/u_resolution.x, 0);
  
  color = vec4(
  	sampleOriginalTexture(uv*.5+.5 + offset*-1).r,
  	sampleOriginalTexture(uv*.5+.5 + offset*+0).g,
  	sampleOriginalTexture(uv*.5+.5 + offset*+1).b,
  	1
  );
}
