#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 projection  = mat4(1);
uniform mat4 view  = mat4(1);
uniform mat4 model = mat4(1);

void main()
{
    gl_Position = projection * view * model * vec4(pos.x, pos.y, pos.z, 1.0);
}


