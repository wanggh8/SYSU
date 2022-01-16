#include "main.h"

void error_callback(int error, const char* description) {
	fputs(description, stderr);
}

bool check_time() {
	new_time = clock();
	if (new_time - start_time > speed) {
		start_time = new_time;
		return true;
	}
	return false;
}

void key_callback(GLFWwindow* window, int key,
	int scancode, int action, int mods) {
	if (action == GLFW_PRESS) {
		if (key == GLFW_KEY_LEFT) {
			gameBoard.rotate(rotateStep);
			snake.relativeRotate(gameBoard.getM(), rotateStep);
			apple.relativeRotate(gameBoard.getM(), rotateStep);
		}
		if (key == GLFW_KEY_RIGHT) {
			gameBoard.rotate(-rotateStep);
			snake.relativeRotate(gameBoard.getM(), -rotateStep);
			apple.relativeRotate(gameBoard.getM(), -rotateStep);
		}
		if (key == GLFW_KEY_A) {
			if (snake.getTurn() == 0) {
				snake.turnLeft();
			}
		}
		if (key == GLFW_KEY_D) {
			if (snake.getTurn() == 0) {
				snake.turnRight();
			}
		}
		if (key == GLFW_KEY_EQUAL) zoom -= 0.5;
		if (key == GLFW_KEY_MINUS) zoom += 0.5;
		if (key == GLFW_KEY_W) {
			if (speed != 100) speed -= 100;
		}
		if (key == GLFW_KEY_S) speed += 100;
	}
}



void initOpenGLProgram(GLFWwindow* window) {
	glEnable(GL_LIGHTING); 
	
	glEnable(GL_LIGHT0);
	GLfloat light1_ambient[] = { 1, 1, 1, 1.0 };
	GLfloat light1_diffuse[] = { 0.2f, 1.0f, 1.0f, 1.0f };
	GLfloat light1_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	GLfloat light1_position[] = { -2.0f, 2.0f, 1.0f, 1.0f };
	GLfloat spot_direction[] = { -1.0f, -1.0f, 0.0f };

	glLightfv(GL_LIGHT1, GL_AMBIENT, light1_ambient);
	glLightfv(GL_LIGHT1, GL_DIFFUSE, light1_diffuse);
	glLightfv(GL_LIGHT1, GL_SPECULAR, light1_specular);
	glLightfv(GL_LIGHT1, GL_POSITION, light1_position);
	glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 1.5);
	glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, 10);
	glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, 0.2);

	glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 45.0);
	glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spot_direction);
	glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, 2.0);

	glEnable(GL_LIGHT1);

	glShadeModel(GL_FLAT);
	glEnable(GL_DEPTH_TEST);
	glColorMaterial(GL_FRONT, GL_DIFFUSE);

	glEnable(GL_COLOR_MATERIAL);

	glColor3f(1, 1, 1);

	glGenTextures(4, &tex[0]);
	
	gameBoard.init("floor.png", "floor.obj");
	apple.init("apple.png", "apple.obj");
	snake.init("snake.png", "cube.obj");
	snake.setInitPosition(0, 1, 0);

	M = glm::mat4(1.0f);
	V = lookAt(
		glm::vec3(0, zoom, -2 * zoom),
		glm::vec3(0, 0, 0),
		glm::vec3(0, 1, 0)
	);
	P = glm::perspective(50 * PI / 180, 1.0f, 1.0f, 50.0f);

	std::vector<Coordinate> coordinates;
	for (auto &snakePart : snake.getSnakeParts()) {
		coordinates.push_back(snakePart.getCurrentPosition2());
	}

	apple.respawnInNewPlace(4, &gameBoard, coordinates);
	
	glfwSetKeyCallback(window, key_callback);
}

void drawScene(GLFWwindow* window) {	
	if (gameBoard.isLoose()) {
		string tmp;
		sprintf_s((char*)tmp.c_str(),sizeof((char*)tmp.c_str()),  "%d", gameBoard.getPoints());
		string str = tmp.c_str();
		
	}
	V = lookAt(
		glm::vec3(0, zoom, -2 * zoom),
		glm::vec3(0, 0, 0),
		glm::vec3(0, 1, 0)
	);

	glClearColor(0, 0, 0, 1);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
	glMatrixMode(GL_PROJECTION);
	glLoadMatrixf(value_ptr(P));

	gameBoard.draw(&V);

	apple.draw(&V);

	snake.draw(&V);

	if (check_time()) {
		snake.move(&gameBoard, &apple);
	}
	
	glfwSwapBuffers(window);
}

int main(void)
{
	srand(time(NULL));
	GLFWwindow* window; 

	glfwSetErrorCallback(error_callback);

	if (!glfwInit()) { 
		fprintf(stderr, "error GLFW.\n");
		exit(EXIT_FAILURE);
	}

	window = glfwCreateWindow(1300, 760, "Snake", NULL, NULL); 
	if (!window) 
	{
		glfwTerminate();
		exit(EXIT_FAILURE);
	}

	glfwMakeContextCurrent(window);
	glfwSwapInterval(1); 

	GLenum err;
	if ((err = glewInit()) != GLEW_OK) { 	
		fprintf(stderr, "GLEW: %s\n", glewGetErrorString(err));
		exit(EXIT_FAILURE);
	}

	initOpenGLProgram(window); 

						 
	while (!glfwWindowShouldClose(window)) 
	{
		drawScene(window); 
		glfwPollEvents(); 
	}

	glfwDestroyWindow(window); 
	glfwTerminate(); 
	exit(EXIT_SUCCESS);
}