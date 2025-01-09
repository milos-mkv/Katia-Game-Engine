#version 330 core

out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D uTexture;
uniform bool isCamera;
uniform vec3 bgColor;

void main() {
    if (isCamera) {
        FragColor = vec4(bgColor, 1);
    } else {
        FragColor = texture(uTexture, TexCoord);
    }
}