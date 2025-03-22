#version 330 core
out int FragColor;
uniform int selectId;
in vec2 vUV;
uniform sampler2D uTexture; // Font atlas texture

void main() {
    float alpha = texture(uTexture, vUV).a; // Use the red channel for alpha (font glyphs are grayscale)

    // Discard low-alpha pixels for crisp text rendering
    if (alpha < 0.1) {
        discard;
    }

    FragColor = selectId;
}