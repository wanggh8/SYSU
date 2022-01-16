#pragma once
#include "Coordinate.h"
#include "GameObject.h"

class GameBoard : public GameObject
{
private:
	Coordinate foodPosition;
	Coordinate snakeHeadPosition;
	float degree;
	bool loose;
	int points;
public:
	GameBoard();
	void rotate(float degree);
	void updateFoodPosition(Coordinate newCords);
	void updateSnakeHeadPosition(Coordinate newCords);
	bool comparePosition(float radius, float firstX, float secondX);
	Coordinate getFoodPosition();
	Coordinate getSnakeHeadPosition();
	bool checkIfSnakeAteFood();
	float getDegree();
	bool isLoose();
	void setLoose();
	int getPoints();
	void increasePoints();
	~GameBoard();
};

