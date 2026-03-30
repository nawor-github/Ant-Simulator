#version 410

in float v_xCoord;
in vec3 v_colour; // vertex colour (r,g,b)

layout(location = 0) out vec4 o_colour;	// RGBA output to colour buffer 



void main() {
	vec4 colour = vec4(v_colour.x, v_colour.y, v_colour.z, 1);
	o_colour = colour;
}
