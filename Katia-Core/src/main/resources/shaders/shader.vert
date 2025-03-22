#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 aTexCoord;

out vec2 TexCoord;
out vec2 vUV;
uniform mat4 model, view, projection;

void main() {
    TexCoord = aTexCoord;
    gl_Position = projection * view * model * vec4(pos, 1);
    vUV = aTexCoord; // Pass UVs to fragment shader

}
