#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform float Time;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

out vec4 fragColor;

in float vertexDistance;
in vec2 texCoord0;

void main() {
    // The initial idea was to glow some pixels in the middle of our texture
    // with a slight pulsating effect. But after some headaches, I gave up
    vec4 color = texture(Sampler0, texCoord0);

    fragColor = color ;
}
