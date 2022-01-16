#pragma once
class Coordinate
{
private:
	float x;
	float y;
public:
	Coordinate();
	float getX();
	float getY();
	void setX(float x);
	void setY(float y);
	void updateCords(Coordinate newCords);
	~Coordinate();
};

