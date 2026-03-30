#version 410

in float v_xCoord;

uniform vec3 u_colour; // vertex colour (r,g,b)
uniform vec3 u_stripeColour; // vertex colour (r,g,b) <-- used for stripes

layout(location = 0) out vec4 o_colour;	// RGBA output to colour buffer 



void main() {
	//This next line implements stripes by checking a bunch of x_coordinate pairs and shading between them
	if ((v_xCoord > -0.1 && v_xCoord < 0) || (v_xCoord > 0.25 && v_xCoord < 0.35) || (v_xCoord > 0.15 && v_xCoord < 0.2) || (v_xCoord > 0.05 && v_xCoord < 0.1)){
	    o_colour = vec4(u_stripeColour, 1);
	}
	//If it's not between any of the pairs, just colour it the base colour
	else {
		o_colour = vec4(u_colour, 1);
	}
}
