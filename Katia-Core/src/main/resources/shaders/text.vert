// Vertex Shader: font_vertex_instanced.glsl
#version 330 core

layout(location = 0) in vec2 aVertexPosition; // Static vertex position (quad corners)
layout(location = 1) in vec2 aVertexUV;       // Static texture coordinates (s, t)
layout(location = 2) in mat4 aInstanceModel;  // Instance model matrix (per character)
layout(location = 6) in vec4 aInstanceUV;     // Instance texture coordinates (s0, t0, s1, t1)

uniform mat4 proj; // Model-View-Projection matrix
uniform mat4 view;
float map(float value, float start1, float stop1, float start2, float stop2) {
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}
out vec2 vUV; // Interpolated texture coordinates passed to the fragment shader

void main() {
    // Apply the instance model matrix and MVP to transform the vertex position
    vec4 worldPosition = aInstanceModel * vec4(aVertexPosition, 0.0, 1.0);
    gl_Position = proj * view * worldPosition;

    // Interpolate UV coordinates based on vertex position
    vec2 localUV = aVertexUV;
    vUV = vec2(
    map(aVertexUV.x, 0, 1, aInstanceUV.x, aInstanceUV.z),
    map(aVertexUV.y, 0, 1, aInstanceUV.y, aInstanceUV.w));
}