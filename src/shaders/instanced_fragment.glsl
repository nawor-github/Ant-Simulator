#version 410

in float v_xCoord;
int vec3 v_Colour; // vertex colour (r,g,b)

layout(location = 0) out vec4 o_colour;	// RGBA output to colour buffer 



void main() {
	o_colour = v_colour;
}
