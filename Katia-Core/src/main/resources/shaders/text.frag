// Fragment Shader: font_fragment_instanced.glsl
#version 330 core

in vec2 vUV; // Interpolated texture coordinates from the vertex shader

out vec4 fragColor; // Output fragment color

uniform sampler2D uTexture; // Font atlas texture
uniform vec4 uFontColor;    // Font color (RGBA)

void main() {
    // Sample the font texture at the given UV coordinates
    float alpha = texture(uTexture, vUV).r; // Use the red channel for alpha (font glyphs are grayscale)

    // Discard low-alpha pixels for crisp text rendering
    if (alpha < 0.1) {
        discard;
    }

    // Output the color with alpha modulation
    fragColor = vec4(uFontColor.rgb, alpha * uFontColor.a);
}
