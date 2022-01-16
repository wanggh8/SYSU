#include "Snake.h"

void Snake::init(char *pathImage, char *pathObj) {
	this->snakeParts[0].init(pathImage, pathObj);

	std::vector<unsigned char> image;   
	unsigned width, height;   
	unsigned imageTextureLoadingStatus = lodepng::decode(image, width, height, "snakepart.png");

	glGenTextures(1, &this->tex); 
	glBindTexture(GL_TEXTURE_2D, this->tex); 
											 
	glTexImage2D(GL_TEXTURE_2D, 0, 4, width, height, 0,
		GL_RGBA, GL_UNSIGNED_BYTE, (unsigned char*)image.data());

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	glEnable(GL_TEXTURE_2D);
} 

void Snake::setInitPosition(float x, float y, float z)
{
	this->snakeParts[0].setInitPosition(x, y, z);
}

void Snake::turnLeft()
{
	this->turn = 1;
}

void Snake::turnRight()
{
	this->turn = 2;
}

void Snake::dontTurn()
{
	this->turn = 0;
}

short Snake::getTurn()
{
	return this->turn;
}

std::vector<SnakePart> Snake::getSnakeParts()
{
	return this->snakeParts;
}


void Snake::move(GameBoard *gameBoard, Food *food) {
	switch (this->turn) {
	case 1:
		rotate(PI / 2, 1);
		break;
	case 2:
		rotate(-PI / 2, -1);
		break;
	}
	dontTurn();

	if (this->growing) {
		gameBoard->increasePoints();
		this->grow();
	}

	for (int i = length - 1; i >= 0; i--) {
		if (snakeParts[i].isHead()) {
			snakeParts[i].move(gameBoard, this->snakeParts);
		}
		else {
			snakeParts[i].move(snakeParts[i-1].getCurrentPosition(), gameBoard->getM(), gameBoard->getDegree());
		}
	}
	
	if (gameBoard->checkIfSnakeAteFood()) {
		std::vector<Coordinate> coordinates;
		for (auto &snakePart : snakeParts) {
			coordinates.push_back(snakePart.getCurrentPosition2());
		}
		food->respawnInNewPlace(4, gameBoard, coordinates);
		this->growing = true;
	}
}

void Snake::rotate(float degree, float direction) {
	this->snakeParts[0].rotate(degree, direction);
}

void Snake::relativeRotate(glm::mat4 *relativeM, float degree) {
	for (auto &snakePart : snakeParts) {
		snakePart.relativeRotate(relativeM, degree);
	}
}

void Snake::grow() {
	glm::mat4 M = glm::mat4(1.0f);
	SnakePart snakePart(&M, false, snakeParts[length-1].getDirection(), &this->tex);

	this->snakeParts.push_back(snakePart);
	
	this->length++;
	this->growing = false;
} 

void Snake::draw(glm::mat4 *V)
{
	for (auto &snakePart : snakeParts) {
		snakePart.draw(V);
	}
}

Snake::~Snake()
{
}
