#include "Coordinate.h"



Coordinate::Coordinate()
{
}
float Coordinate::getX() {
	return this->x;
}
float Coordinate::getY() {
	return this->y;
}
void Coordinate::setX(float x) {
	this->x = x;
}
void Coordinate::setY(float y) {
	this->y = y;

}

void Coordinate::updateCords(Coordinate newCords) {
	this->x = newCords.getX();
	this->y = newCords.getY();
}

Coordinate::~Coordinate()
{
}
