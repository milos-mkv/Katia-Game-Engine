#version 330 core
out int FragColor;
uniform int selectId;

void main() {
    FragColor = selectId;
}