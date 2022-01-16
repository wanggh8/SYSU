#include "GameObject.h"
#include "lodepng.h"
#include <stdio.h>
#include <stdlib.h>
#include <ctime>

GameObject::GameObject()
{
	this->M = glm::mat4(1.0f);

}


GameObject::GameObject(glm::mat4 *startingWorldMatrix)
{
	this->M = *startingWorldMatrix;

	this->currentPosition.setX(0);
	this->currentPosition.setY(0);
}

void GameObject::draw(glm::mat4 *V) {
	glMatrixMode(GL_MODELVIEW);
	glLoadMatrixf(value_ptr(*V * this->M));
	glBindTexture(GL_TEXTURE_2D, this->tex);
	this->model.drawModel();
}


void GameObject::init(char *pathImage, char *pathObj) {
	this->model.init(pathObj, this->M);

	std::vector<unsigned char> image;   
	unsigned width, height;   
	unsigned imageTextureLoadingStatus = lodepng::decode(image, width, height, pathImage);

	glGenTextures(1, &this->tex); 
	glBindTexture(GL_TEXTURE_2D, this->tex); 
												
	glTexImage2D(GL_TEXTURE_2D, 0, 4, width, height, 0,
		GL_RGBA, GL_UNSIGNED_BYTE, (unsigned char*)image.data());

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	glEnable(GL_TEXTURE_2D);
	
	
}

void GameObject::setInitPosition(float x, float y, float z) {
	this->M = glm::translate(this->M, glm::vec3(x, y, z));
}

Coordinate *GameObject::getCurrentPosition()
{
	return &this->currentPosition;
}

Coordinate & GameObject::getCurrentPosition2()
{
	return this->currentPosition;
}

glm::mat4 *GameObject::getM() {
	return &this->M;
}


GameObject::~GameObject()
{
}
