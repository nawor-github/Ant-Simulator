#version 410

in float v_xCoord;

uniform vec3 u_colour; // vertex colour (r,g,b)
uniform vec3 u_stripeColour; // vertex colour (r,g,b) <-- used for stripes

layout(location = 0) out vec4 o_colour;	// RGBA output to colour buffer 



void main() {
	o_colour = a_colour;
}
