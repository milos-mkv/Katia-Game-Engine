#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 model, view, projection;

void main() {
    gl_Position = projection * view * model * vec4(pos, 1);
}
