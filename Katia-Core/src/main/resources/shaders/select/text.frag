#version 330 core
//in vec2 TexCoord;
out int FragColor;
//uniform isampler2D framebufferTexture;
uniform int selectId;

void main() {
    FragColor = selectId; // Map ID to red
}