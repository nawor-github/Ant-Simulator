#version 410

in vec4 a_position; // The vertex position from our java class

uniform mat4 u_mvpMatrix;

void main() {
	// Passing the data into the fragment shader
	gl_Position = u_mvpMatrix * a_position;
}
