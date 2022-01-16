#ifndef MAIN_H
#define MAIN_H
#include "GL/glew.h"
#include "GL/glut.h"
#include "glm/glm.hpp"
#include "GLFW/glfw3.h"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "Model.h"
#include "Constants.h"
#include "lodepng.h"
#include "Food.h"
#include "Snake.h"
#include "GameBoard.h"
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctime>
#include <string>
#include <sstream>

GLuint tex[4];
glm::mat4 V, V1, M = glm::mat4(1.0f), P;
int speed = 1000;
const float rotateStep = 0.1;
glm::mat4 M2 = glm::mat4(1.0f);
Food apple;
Snake snake(&M2);
GameBoard gameBoard;
clock_t new_time, start_time = clock();
float zoom = 12.5;

#endif