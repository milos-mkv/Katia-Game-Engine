//#version 330 core
//
//out int c;
//
//uniform int selectId;
//
//void main() {
//    c = selectId;
//}

#version 330 core
//in vec2 TexCoord;
out int FragColor;
//uniform isampler2D framebufferTexture;
uniform int selectId;

void main() {
    FragColor = selectId; // Map ID to red
}