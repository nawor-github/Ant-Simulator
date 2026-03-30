#version 410

in vec4 a_position; // The vertex position from our java class
in vec3 a_colour; // Vertex colour

uniform mat4 u_mvpMatrix;

out vec3 v_colour;

void main() {
 	v_colour = a_colour;
	// Passing the data into the fragment shader
	gl_Position = u_mvpMatrix * a_position;
}
