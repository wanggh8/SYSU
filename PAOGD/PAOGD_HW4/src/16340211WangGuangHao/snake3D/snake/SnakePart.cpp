#include "SnakePart.h"

void SnakePart::move(GameBoard *gameBoard, std::vector <SnakePart> &snakeParts) {
	int moveValue = 2;
	float newPositionX = this->currentPosition.getX();
	float newPositionY = this->currentPosition.getY();

	if (this->direction % 2 == 0) {
		//forward
		if (this->direction == 0) {
			newPositionX += 2;
		}
		//backward
		else {
			newPositionX -= 2;
			
		}
		this->currentPosition.setX(newPositionX);
	}

	else {
		if (this->direction == 1 || this->direction == -3) {
			newPositionY -= 2;
			
		}
		else {
			newPositionY += 2;
		
		}
		this->currentPosition.setY(newPositionY);
	}
	if (newPositionX == 10 || newPositionX == -10 || newPositionY == 10 || newPositionY == -10) {
		gameBoard->setLoose();
	}
	for (int i = snakeParts.size() - 1; i > 0; i --) {
		if (newPositionX == snakeParts[i].getCurrentPosition()->getX() && newPositionY == snakeParts[i].getCurrentPosition()->getY()) {
			gameBoard->setLoose();
		}
	}
	this->M = glm::translate(this->M, glm::vec3(2.0f, 0, 0));
	gameBoard->updateSnakeHeadPosition(this->currentPosition);
}

void SnakePart::move(Coordinate * coordinate, glm::mat4 * relativeM, float degree) {
	this->M = glm::mat4(1.0f);
	this->M = glm::rotate(*relativeM, 0.1f, glm::vec3(0, 1.0f, 0));
	this->M = glm::translate(*relativeM, glm::vec3(0, 1.0f, 0));

	this->M = glm::translate(this->M, glm::vec3(coordinate->getX(), 0.0f, coordinate->getY()));
	this->currentPosition.setX(coordinate->getX());
	this->currentPosition.setY(coordinate->getY());
}

void SnakePart::rotate(float degree, float direction) {
	this->direction += direction;
	if (this->direction == 4 || this->direction == -4) {
		this->direction = 0;
	}
	this->M = glm::rotate(this->M, degree, glm::vec3(0, 1.0f, 0));
}

void SnakePart::relativeRotate(glm::mat4 * relativeM, float degree) {
	glm::mat4 previousState = this->M;
	this->M = glm::rotate(*relativeM, degree, glm::vec3(0, 1.0f, 0));
	const float PI = 3.141592653589793f;
	this->M = glm::translate(*relativeM, glm::vec3(this->currentPosition.getX(), 1.0f, this->currentPosition.getY()));
	if (this->direction % 2 == 0) {
		//forward
		if (this->direction == 0) {
			this->M = glm::rotate(this->M, 0.0f, glm::vec3(0, 1.0f, 0));
		}
		//backward
		else {
			this->M = glm::rotate(this->M, PI, glm::vec3(0, 1.0f, 0));
		}
	}
	
	else {
		if (this->direction == 1 || this->direction == -3) {
			this->M = glm::rotate(this->M, PI / 2, glm::vec3(0, 1.0f, 0));
		}
		else {
			this->M = glm::rotate(this->M, -PI / 2, glm::vec3(0, 1.0f, 0));
		}
	}
}

bool SnakePart::isHead()
{
	return this->head;
}

int SnakePart::getDirection()
{
	return this->direction;
}

SnakePart::~SnakePart()
{
}
