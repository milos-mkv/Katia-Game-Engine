#version 330 core

layout (location = 0) in vec3 pos;

uniform mat4 model, view, projection;
out vec3 color;
void main() {
    if (pos.x != 0) {
        float newx = 0;
        if (pos.x < 0) {
            newx = -1;
        } else {
            newx = 1;
        }
        gl_Position = projection * view * vec4(pos, 1.0);
        gl_Position.x = newx;
        color = vec3(1, 0, 0);
    }
    if (pos.y != 0) {
        float newy = 0;
        if (pos.y < 0) {
            newy = -1;
        } else {
            newy = 1;
        }
        gl_Position = projection * view * vec4(pos, 1.0);
        gl_Position.y = newy;
        color = vec3(0, 1, 0);
    }
}
