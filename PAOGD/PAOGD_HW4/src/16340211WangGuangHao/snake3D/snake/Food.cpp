#pragma once
#include "Food.h"

void Food::relativeRotate(glm::mat4 * relativeM, float degree)
{
	glm::mat4 previousState = this->M;
	this->M = glm::rotate(*relativeM, degree, glm::vec3(0, 1.0f, 0));
	const float PI = 3.141592653589793f;
	this->M = glm::translate(*relativeM, glm::vec3(this->currentPosition.getX(), 1.0f, this->currentPosition.getY()));
	
}

void Food::respawnInNewPlace(int maxAbasoluteRandNumber, GameBoard *gameBoard, std::vector<Coordinate> &coordinates) {
	srand(time(NULL));
	bool emptyPosition;
	int randomTranslateX;
	int randomTranslateY;

	do {
		randomTranslateX = 2 * ((rand() % (2 * maxAbasoluteRandNumber)) - maxAbasoluteRandNumber);
		randomTranslateY = 2 * ((rand() % (2 * maxAbasoluteRandNumber)) - maxAbasoluteRandNumber);

		emptyPosition = true;
		for (int i = coordinates.size()-1; i >= 0; i --) {
			
			if (coordinates[i].getX() == randomTranslateX && coordinates[i].getY() == randomTranslateY) {
				emptyPosition = false;
				break;
			}
		}

	} while (!emptyPosition);

	this->M = glm::translate(*gameBoard->getM(), glm::vec3(randomTranslateX, 1, randomTranslateY));
	float newX = randomTranslateX;
	float newY = randomTranslateY;
	this->currentPosition.setX(newX);
	this->currentPosition.setY(newY);
	gameBoard->updateFoodPosition(this->currentPosition);
};

Food::~Food()
{
}
