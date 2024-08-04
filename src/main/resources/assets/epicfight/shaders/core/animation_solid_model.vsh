#version 150

#moj_import <light.glsl>

in vec3 Position;
in vec3 Normal;
in ivec3 Joints;
in vec3 Weights;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

uniform vec4 Color;
uniform mat3 Normal_Mv_Matrix;
uniform mat4 Poses[50];

out vec4 vertexColor;
out vec4 normal;

void main() {
	vec3 Position_a;
	vec3 Normal_a;
	
	for(int i=0;i<3;i++)
    {
        mat4 jointTransform = Poses[Joints[i]];
        vec4 posePosition = jointTransform * vec4(Position, 1.0);
        Position_a += vec3(posePosition.xyz) * Weights[i];
    }
	
	for(int i=0;i<3;i++)
    {
        mat4 jointTransform = Poses[Joints[i]];
        vec4 poseNormal = jointTransform * vec4(Normal, 1.0);
        Normal_a += vec3(poseNormal.xyz) * Weights[i];
    }
	
	Normal_a = Normal_Mv_Matrix * Normal_a;
	
    gl_Position = ProjMat * ModelViewMat * vec4(Position_a, 1.0);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal_a, Color);
    normal = ProjMat * ModelViewMat * vec4(Normal_a, 0.0);
}
