#version 410

in vec2 v_localCoord;
in vec3 v_colour; // vertex colour (r,g,b)

uniform vec3 u_edgeColour; // vertex colour (r,g,b) <-- Used for edging squares

layout(location = 0) out vec4 o_colour;	// RGBA output to colour buffer 



void main() {
	if (v_localCoord.x < 0.1 || v_localCoord.x > 0.9 || v_localCoord.y < 0.1 || v_localCoord.y > 0.9) {
		o_colour = vec4(u_edgeColour, 1);
	}
	else {
		o_colour = vec4(v_colour, 1);
	}
}
