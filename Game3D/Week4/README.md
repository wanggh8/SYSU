# 简答并用程序验证
## 1.游戏对象运动的本质是什么？
游戏对象运动的本质就是在每一帧的画面中，游戏对象会发生相对位置或绝对位置的改变，或者是角度的旋转变化，即transform属性中的position跟rotation两个属性。
## 2.请用三种方法以上方法，实现物体的抛物线运动。（如，修改Transform属性，使用向量Vector3的方法…）
####1) 修改Transform属性
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Move : MonoBehaviour
{
    public int time = 1;
	//参数，随时间递增
    // Use this for initialization
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        this.transform.position += Vector3.down * Time.deltaTime * (time / 10);
		//上下方向匀加速运动
        this.transform.position += Vector3.left * Time.deltaTime * 5;
		//左右方向匀速运动
        time++;
    }
}
```
#### 2) 直接声明创建一个Vector3变量,将游戏对象原本的position属性与该向量相加
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Move1 : MonoBehaviour
{
    public int time = 1;
    // Use this for initialization
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        Vector3 a = new Vector3(Time.deltaTime * 5, -Time.deltaTime * (time / 10), 0);
        //声明一个变化的向量Vector3
        this.transform.position += a;
        time++;
    }
}
```
#### 3) 直接声明创建一个Vector3变量,利用transform中的translate函数来进行改变position
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Move2 : MonoBehaviour
{
    public int time = 1;
    // Use this for initialization
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        Vector3 a = new Vector3(0, -Time.deltaTime * 5, Time.deltaTime * (time / 10));
        //声明一个变化的向量Vector3
        this.transform.Translate(a);
        time++;
    }
}
```

结果

![ans1](https://img-blog.csdn.net/20180402204044288?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## 3.写一个程序，实现一个完整的太阳系， 其他星球围绕太阳的转速必须不一样，且不在一个法平面上。
对于月球的轨迹问题，采用建立空对象的方法
使用一个空对象作为地球的影子
将月球挂在这个空对象上
空对象与地球位置保持一致
对象的安排

![2](https://img-blog.csdn.net/20180403135932319?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

素材

![3](https://img-blog.csdn.net/20180403140024734?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

代码

![4](https://img-blog.csdn.net/20180403140102998?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

将Empty挂到空对象上，Sun挂到Sun对象上，Month挂到month对象上

sun.cs
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Sun : MonoBehaviour
{

    // Use this for initialization
    void Start()
    {
        GameObject.Find("Sun").transform.position = new Vector3(0, 0, 0);

        GameObject.Find("Mercury").transform.position = new Vector3(0, 0, 2);
        GameObject.Find("Venus").transform.position = new Vector3(2.8f, 0, 0);
        GameObject.Find("Earth").transform.position = new Vector3(0, 0, 3.5f);
        GameObject.Find("Mars").transform.position = new Vector3(0, 0, 4.2f);
        GameObject.Find("Jupiter").transform.position = new Vector3(0, 0, 4.8f);
        GameObject.Find("Saturn").transform.position = new Vector3(5.5f, 0, 0);
        GameObject.Find("Uranus").transform.position = new Vector3(6.5f, 0, 0);
        GameObject.Find("Neptune").transform.position = new Vector3(0, 7.5f, 0);
    }

    // Update is called once per frame
    void Update()
    {
        GameObject.Find("Earth").transform.Rotate(Vector3.up * Time.deltaTime * 500);
        GameObject.Find("Earth").transform.RotateAround(Vector3.zero, new Vector3(0, 1, 0), 30 * Time.deltaTime);

        GameObject.Find("Mercury").transform.RotateAround(Vector3.zero, new Vector3(1, 1, 0), 25 * Time.deltaTime);
        GameObject.Find("Mercury").transform.Rotate(Vector3.up * Time.deltaTime * 500);

        GameObject.Find("Venus").transform.RotateAround(Vector3.zero, new Vector3(0, 1, 1), 20 * Time.deltaTime);
        GameObject.Find("Venus").transform.Rotate(Vector3.up * Time.deltaTime * 500);

        GameObject.Find("Mars").transform.RotateAround(Vector3.zero, new Vector3(2, 1, 0), 45 * Time.deltaTime);
        GameObject.Find("Mars").transform.Rotate(Vector3.up * Time.deltaTime * 500);

        GameObject.Find("Jupiter").transform.RotateAround(Vector3.zero, new Vector3(1, 2, 0), 35 * Time.deltaTime);
        GameObject.Find("Jupiter").transform.Rotate(Vector3.up * Time.deltaTime * 500);

        GameObject.Find("Saturn").transform.RotateAround(Vector3.zero, new Vector3(0, 1, 2), 40 * Time.deltaTime);
        GameObject.Find("Saturn").transform.Rotate(Vector3.up * Time.deltaTime * 500);

        GameObject.Find("Uranus").transform.RotateAround(Vector3.zero, new Vector3(0, 2, 1), 45 * Time.deltaTime);
        GameObject.Find("Uranus").transform.Rotate(Vector3.up * Time.deltaTime * 500);

        GameObject.Find("Neptune").transform.RotateAround(Vector3.zero, new Vector3(1, 0, 1), 50 * Time.deltaTime);
        GameObject.Find("Neptune").transform.Rotate(Vector3.up * Time.deltaTime * 500);

    }
}
```

Empty.cs
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Empty : MonoBehaviour
{

    // Use this for initialization
    void Start()
    {
        this.transform.position = new Vector3(0, 0, 3.5f);
    }

    // Update is called once per frame
    void Update()
    {
        this.transform.RotateAround(Vector3.zero, new Vector3(0, 1, 0), 30 * Time.deltaTime);
    }
}

```

Month.cs
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Month : MonoBehaviour {

	// Use this for initialization
	void Start () {
        this.transform.position = new Vector3(0, 0, 4.5f);
	}
	
	// Update is called once per frame
	void Update () {
        Vector3 position = this.transform.parent.position;

        this.transform.RotateAround(position, Vector3.up, 360 * Time.deltaTime);
	}
}

```

结果

![5](https://img-blog.csdn.net/20180403140438391?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
视频展示


# 编程实践
### 阅读以下游戏脚本
Priests and Devils
Priests and Devils is a puzzle game in which you will help the Priests and Devils to cross the river within the time limit. There are 3 priests and 3 devils at one side of the river. They all want to get to the other side of this river, but there is only one boat and this boat can only carry two persons each time. And there must be one person steering the boat from one side to the other side. In the flash game, you can click on them to move them and click the go button to move the boat to the other direction. If the priests are out numbered by the devils on either side of the river, they get killed and the game is over. You can try it in many > ways. Keep all priests alive! Good luck!
### 程序需要满足的要求：
#### play the game ( http://www.flash-game.net/game/2535/priests-and-devils.html )
#### 列出游戏中提及的事物（Objects）
牧师、魔鬼、船、初始岸边、目的岸边
#### 用表格列出玩家动作表（规则表），注意，动作越少越好
动作 | 条件
- | :-: |
开船到左岸| 船上有人，船在右岸
开船到右岸| 船上有人，船在左岸
下船到左岸 |船上有人，船在左岸
左岸上船 |船上有空位，船在左岸
下船到右岸 |船上有人，船在右岸
右岸上船 |船上有空位，船在右岸
重开游戏| 游戏结束失败时

#### 请将游戏中对象做成预制
将魔鬼、牧师、船、岸边做成预制

![6](https://img-blog.csdn.net/20180403210408236?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### 在 GenGameObjects 中创建 长方形、正方形、球 及其色彩代表游戏中的对象。
游戏对象

![7](https://img-blog.csdn.net/20180403210616661?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### 使用 C# 集合类型 有效组织对象

#### 整个游戏仅 主摄像机 和 一个 Empty 对象， 其他对象必须代码动态生成！！！ 。 整个游戏不许出现 Find 游戏对象， SendMessage 这类突破程序结构的 通讯耦合 语句。 违背本条准则，不给分
![8](https://img-blog.csdn.net/20180403210734983?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
动态生成对象
```
Camera.main.transform.position = new Vector3(0, 0, -20);
        GameObject priest_obj, devil_obj;

        rightSide_obj = Instantiate(Resources.Load("Prefabs/Side"), RightSidePos,
         Quaternion.identity) as GameObject;
        leftSide_obj = Instantiate(Resources.Load("Prefabs/Side"), LeftSidePos,
        Quaternion.identity) as GameObject;

        rightSide_obj.name = "rightSide";
        leftSide_obj.name = "leftSide";

        boat_obj = Instantiate(Resources.Load("Prefabs/boat"), BoatRightPos,
        Quaternion.identity) as GameObject;
        boat[0] = null;
        boat[1] = null;
        boat_obj.name = "boat";
        rightSide[6] = boat_obj;
        leftSide[6] = null;
        //实例化岸边和船
        for (int i = 0; i < 3; i++)
        {
            priest_obj = Instantiate(Resources.Load("Prefabs/Priest")) as GameObject;
            priest_obj.name = i.ToString();//牧师编号0 1 2
            priest_obj.transform.position = new Vector3(4.5f + 1f * i, -0.9f, 0);
            rightSide[i] = priest_obj;

            devil_obj = Instantiate(Resources.Load("Prefabs/Devil")) as GameObject;
            devil_obj.name = (i + 3).ToString();//魔鬼编号3 4 5
            devil_obj.transform.position = new Vector3(7.5f + 1f * i, -0.9f, 0);
            rightSide[i + 3] = devil_obj;
        }
        //实例化魔鬼和牧师
```

#### 请使用课件架构图编程，不接受非 MVC 结构程序
![9](https://img-blog.csdn.net/2018040321092037?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

SSDirector对象 获取当前游戏的场景、控制场景运行、管理游戏全局状态、设定游戏全局视图
View 负责UI和可视化
FirstSceneController对象 管理本次场景所有的游戏对象、协调游戏对象（预制件级别）之间的通讯、响应外部输入事件、管理本场次的规则（裁判）。


#### 注意细节，例如：船未靠岸，牧师与魔鬼上下船运动中，均不能接受用户事件！
```
/*
public enum State
{
    win, fail, start, still, toLeft, toRight, getOn, getOff
}
*/


 if (Input.GetMouseButtonDown(0) &&
            (SSDirector.getInstance().state == State.start || SSDirector.getInstance().state == State.still))
        {
            Debug.Log("2");
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit;
            if (Physics.Raycast(ray, out hit))
            {
                gameObj = hit.transform.gameObject;
            }
        }
```
通过确认游戏的状态state在still和start时，响应鼠标的点击行为

以下是完整代码
SSDirector.cs
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public enum State
{
    win, fail, start, still, toLeft, toRight, getOn, getOff
}
public class SSDirector : System.Object
{
    public int leaveSeconds;
    public int move;
    public int move1;
    public State state { get; set; }
    private static SSDirector _instance;
    public bool running { get; set; }
    public ISceneController currentScenceController
    {
        get;
        set;
    }

    public static SSDirector getInstance()
    {
        if (_instance == null)
        {
            _instance = new SSDirector();
        }
        return _instance;
    }
    public int getFPS()
    {
        return Application.targetFrameRate;
    }

    public void setFPS(int fps)
    {
        Application.targetFrameRate = fps;
    }
    public IEnumerator DoCountDown()
    {
        while (leaveSeconds > 0)
        {
            yield return new WaitForSeconds(1f);
            leaveSeconds--;
        }
    }
}
```

FirstSceneController.cs

```

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public interface ISceneController
{
    void LoadResources();
}

public class FirstSceneController : MonoBehaviour, ISceneController, UserAction
{
    Vector3 LeftSidePos = new Vector3(-7, -3.5f, 0);
    Vector3 RightSidePos = new Vector3(7, -3.5f, 0);
    //岸边位置
    Vector3 BoatLeftPos = new Vector3(-2.45f, -3, 0);
    Vector3 BoatRightPos = new Vector3(2.45f, -3, 0);
    //船在两岸的起始位置
    GameObject[] leftSide = new GameObject[7];
    GameObject[] rightSide = new GameObject[7];

    //两岸和岸边的对象
    GameObject boat_obj, leftSide_obj, rightSide_obj;
    GameObject[] boat = new GameObject[2];//船上的对象

    public void LoadResources()//加载资源，实例化对象
    {
        Camera.main.transform.position = new Vector3(0, 0, -20);
        GameObject priest_obj, devil_obj;

        rightSide_obj = Instantiate(Resources.Load("Prefabs/Side"), RightSidePos,
         Quaternion.identity) as GameObject;
        leftSide_obj = Instantiate(Resources.Load("Prefabs/Side"), LeftSidePos,
        Quaternion.identity) as GameObject;

        rightSide_obj.name = "rightSide";
        leftSide_obj.name = "leftSide";

        boat_obj = Instantiate(Resources.Load("Prefabs/boat"), BoatRightPos,
        Quaternion.identity) as GameObject;
        boat[0] = null;
        boat[1] = null;
        boat_obj.name = "boat";
        rightSide[6] = boat_obj;
        leftSide[6] = null;
        //实例化岸边和船
        for (int i = 0; i < 3; i++)
        {
            priest_obj = Instantiate(Resources.Load("Prefabs/Priest")) as GameObject;
            priest_obj.name = i.ToString();//牧师编号0 1 2
            priest_obj.transform.position = new Vector3(4.5f + 1f * i, -0.9f, 0);
            rightSide[i] = priest_obj;

            devil_obj = Instantiate(Resources.Load("Prefabs/Devil")) as GameObject;
            devil_obj.name = (i + 3).ToString();//魔鬼编号3 4 5
            devil_obj.transform.position = new Vector3(7.5f + 1f * i, -0.9f, 0);
            rightSide[i + 3] = devil_obj;
        }
        //实例化魔鬼和牧师

    }
    public void clickOne()
    {
        GameObject gameObj = null;

        if (Input.GetMouseButtonDown(0) &&
            (SSDirector.getInstance().state == State.start || SSDirector.getInstance().state == State.still))
        {
            Debug.Log("2");
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit;
            if (Physics.Raycast(ray, out hit))
            {
                gameObj = hit.transform.gameObject;
            }
        }
        if (gameObj == null)
        {
            return;
        }

        else if (gameObj.name == "0" || gameObj.name == "1" || gameObj.name == "2"
        || gameObj.name == "3" || gameObj.name == "4" || gameObj.name == "5")
        {
            MovePeople(gameObj);
            Debug.Log("1");
        }
        else if (gameObj.name == "boat")
        {
            Debug.Log("4");
            MoveBoat(gameObj);
        }
    }

    public void MovePeople(GameObject p)
    {
        int num = 0;
        if (p.name == "0")
        {
            num = 0;
        }
        if (p.name == "1")
        {
            num = 1;
        }
        if (p.name == "2")
        {
            num = 2;
        }
        if (p.name == "3")
        {
            num = 3;
        }
        if (p.name == "4")
        {
            num = 4;
        }
        if (p.name == "5")
        {
            num = 5;
        }

        if (leftSide[num] != null)
        {

            if (leftSide[6] != null)
            {
                if (boat[0] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = -1 - num;
                    SSDirector.getInstance().move1 = -6;
                    return;
                }
                if (boat[1] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = -1 - num;
                    SSDirector.getInstance().move1 = -7;
                    return;
                }

            }
        }
        if (rightSide[num] != null)
        {
            if (rightSide[6] != null)
            {
                if (boat[0] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = num;
                    SSDirector.getInstance().move1 = 6;
                    return;
                }
                if (boat[1] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = num;
                    SSDirector.getInstance().move1 = 7;
                    return;
                }

            }
        }
        if (leftSide[6] != null && boat[0] != null && boat[0].name == p.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = -6;
            SSDirector.getInstance().move1 = -1 - num;
        }
        if (leftSide[6] != null && boat[1] != null && boat[1].name == p.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = -7;
            SSDirector.getInstance().move1 = -1 - num;
        }
        if (rightSide[6] != null && boat[0] != null && boat[0].name == p.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = 6;
            SSDirector.getInstance().move1 = num;
        }
        if (rightSide[6] != null && boat[1] != null && boat[1].name == p.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = 7;
            SSDirector.getInstance().move1 = num;

        }
    }

    public void MoveBoat(GameObject p)
    {

        if (boat[0] != null || boat[1] != null)
        {
            if (leftSide[6] != null)
            {
                leftSide[6] = null;
                SSDirector.getInstance().state = State.toRight;
            }
            if (rightSide[6] != null)
            {
                rightSide[6] = null;
                SSDirector.getInstance().state = State.toLeft;
            }
            if (leftSide[6] == null && rightSide[6] == null)
            {
                Debug.Log("船正在行驶");
            }
        }
    }
    public void Judge()
    {
        int numPriestLeft = 0;
        int numDevilLeft = 0;
        int numPriestRight = 0;
        int numDevilRight = 0;
        for (int i = 0; i <= 6; i++)
        {
            if (leftSide[i] != null)
            {
                if (i < 3)
                {
                    numPriestLeft++;
                }
                else if (i < 6)
                {
                    numDevilLeft++;
                }
                else
                {
                    if (boat[0] != null)
                    {
                        if (boat[0].tag == "Devil")
                        {
                            numDevilLeft++;
                        }
                        if (boat[0].tag == "Priest")
                        {
                            numPriestLeft++;
                        }
                    }
                    if (boat[1] != null)
                    {
                        if (boat[1].tag == "Devil")
                        {
                            numDevilLeft++;
                        }
                        if (boat[1].tag == "Priest")
                        {
                            numPriestLeft++;
                        }
                    }
                }
            }
            if (rightSide[i] != null)
            {
                if (i < 3)
                {
                    numPriestRight++;
                }
                else if (i < 6)
                {
                    numDevilRight++;
                }
                else
                {
                    if (boat[0] != null)
                    {
                        if (boat[0].tag == "Devil")
                        {
                            numDevilRight++;
                        }
                        if (boat[0].tag == "Priest")
                        {
                            numPriestRight++;
                        }
                    }
                    if (boat[1] != null)
                    {
                        if (boat[1].tag == "Devil")
                        {
                            numDevilRight++;
                        }
                        if (boat[1].tag == "Priest")
                        {
                            numPriestRight++;
                        }
                    }
                }
            }
        }
        if (SSDirector.getInstance().leaveSeconds == 0)
        {
            SSDirector.getInstance().state = State.fail;
            Debug.Log("fail");
            return;
        }
        if (numDevilLeft > numPriestLeft && numPriestLeft != 0)
        {
            SSDirector.getInstance().state = State.fail;

            Debug.Log("fail");
            return;
        }
        if (numDevilRight > numPriestRight && numPriestRight != 0)
        {
            SSDirector.getInstance().state = State.fail;
            Debug.Log("fail");
            return;
        }
        if (numDevilLeft == 3 && numPriestLeft == 3)
        {
            SSDirector.getInstance().state = State.win;
            Debug.Log("win");
            return;
        }
    }
    public void Restart()
    {
        SSDirector.getInstance().state = State.start;
        Application.LoadLevel(Application.loadedLevelName);
        Awake();
    }
    void Awake()
    { 
        SSDirector director = SSDirector.getInstance();
        SSDirector.getInstance().state = State.still;
        SSDirector.getInstance().leaveSeconds = 60;
        director.currentScenceController = this;
        director.currentScenceController.LoadResources();
        director.setFPS(60);

    }
    void Start()
    {
        StartCoroutine(SSDirector.getInstance().DoCountDown());

    }

    // Update is called once per frame
    void Update()
    {

        if (SSDirector.getInstance().state == State.toRight)
        {
            if (boat[0] != null)
            {
                boat[0].transform.position = Vector3.MoveTowards(boat[0].transform.position, new Vector3(2, -1.4f, 0), Time.deltaTime * 5);
            }
            if (boat[1] != null)
            {
                boat[1].transform.position = Vector3.MoveTowards(boat[1].transform.position, new Vector3(3, -1.4f, 0), Time.deltaTime * 5);
            }
            boat_obj.transform.position = Vector3.MoveTowards(boat_obj.transform.position, BoatRightPos, Time.deltaTime * 5);
            if (boat_obj.transform.position == BoatRightPos)
            {
                rightSide[6] = boat_obj;
                SSDirector.getInstance().state = State.still;
            }
            Judge();
        }
        if (SSDirector.getInstance().state == State.toLeft)
        {
            if (boat[0] != null)
            {
                boat[0].transform.position = Vector3.MoveTowards(boat[0].transform.position, new Vector3(-3, -1.4f, 0), Time.deltaTime * 5);
            }
            if (boat[1] != null)
            {
                boat[1].transform.position = Vector3.MoveTowards(boat[1].transform.position, new Vector3(-2, -1.4f, 0), Time.deltaTime * 5);
            }
            boat_obj.transform.position = Vector3.MoveTowards(boat_obj.transform.position, BoatLeftPos, Time.deltaTime * 5);
            if (boat_obj.transform.position == BoatLeftPos)
            {
                leftSide[6] = boat_obj;
                SSDirector.getInstance().state = State.still;
            }
            Judge();
        }
        if (SSDirector.getInstance().state == State.getOn)
        {
            int a = SSDirector.getInstance().move;
            int b = SSDirector.getInstance().move1;
            if (a >= 0)
            {
                rightSide[a].transform.position = Vector3.MoveTowards(rightSide[a].transform.position, new Vector3(b - 4, -1.4f, 0), Time.deltaTime * 5);
                if (rightSide[a].transform.position == new Vector3(b - 4, -1.4f, 0))
                {
                    boat[b - 6] = rightSide[a];
                    rightSide[a] = null;
                    SSDirector.getInstance().state = State.still;
                }
            }
            if (a < 0)
            {
                Debug.Log("WWW");
                leftSide[-a - 1].transform.position = Vector3.MoveTowards(leftSide[-a - 1].transform.position, new Vector3(-b - 9, -1.4f, 0), Time.deltaTime * 5);
                if (leftSide[-a - 1].transform.position == new Vector3(-b - 9, -1.4f, 0))
                {
                    boat[-b - 6] = leftSide[-a - 1];
                    leftSide[-a - 1] = null;
                    SSDirector.getInstance().state = State.still;
                }
            }

        }
        if (SSDirector.getInstance().state == State.getOff)
        {
            int a = SSDirector.getInstance().move;
            int b = SSDirector.getInstance().move1;
            if (b >= 0)
            {
                boat[a - 6].transform.position = Vector3.MoveTowards(boat[a - 6].transform.position, new Vector3(4.5f + 1f * b, -0.9f, 0), Time.deltaTime * 5);
                if (boat[a - 6].transform.position == new Vector3(4.5f + 1f * b, -0.9f, 0))
                {
                    rightSide[b] = boat[a - 6];
                    boat[a - 6] = null;
                    SSDirector.getInstance().state = State.still;
                }
            }
            if (b < 0)
            {
                boat[-a - 6].transform.position = Vector3.MoveTowards(boat[-a - 6].transform.position, new Vector3(-3.5f + 1f * b, -0.9f, 0), Time.deltaTime * 5);
                if (boat[-a - 6].transform.position == new Vector3(-3.5f + 1f * b, -0.9f, 0))
                {
                    leftSide[-b - 1] = boat[-a - 6];
                    boat[-a - 6] = null;
                    SSDirector.getInstance().state = State.still;
                }
            }

        }
        clickOne();

    }
}
```
View.cs
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public interface UserAction
{
    void clickOne();
    void Restart();
}
public class View : MonoBehaviour
{
    SSDirector one;
    UserAction action;
    void Start()
    {
        action = SSDirector.getInstance().currentScenceController as UserAction;
    }

    // Update is called once per frame
    void Update()
    {

    }
    private string result;
    private void OnGUI()
    {
        GUIStyle style1 = GUI.skin.GetStyle("label");
        style1.normal.textColor = new Color(1, 1, 0);
        style1.fontSize = 42;
        string time = SSDirector.getInstance().leaveSeconds.ToString();
        GUI.Label(new Rect(40, 50, 220, 200), time);
        if (SSDirector.getInstance().state == State.win)
        {
            result = "win!!!";
            GUIStyle style = GUI.skin.GetStyle("label");
            style.normal.textColor = new Color(1, 0, 0);
            style.fontSize = 42;
            GUI.Label(new Rect(Screen.width / 2 - 60, 50, 220, 200), result);
        }
        if (SSDirector.getInstance().state == State.still ||
        SSDirector.getInstance().state == State.start)
        {
        }
        if (SSDirector.getInstance().state == State.fail)
        {
            result = "fail!!!";
            GUIStyle style = GUI.skin.GetStyle("label");
            style.normal.textColor = new Color(1, 0, 0);
            style.fontSize = 42;
            GUI.Label(new Rect(Screen.width / 2 - 60, 50, 220, 200), result);
            if (GUI.Button(new Rect(Screen.width / 2 - 60, 100, 80, 30), "Restart"))
            {
                action.Restart();
            }
        }

    }
}

```
# 演示视频
[太阳系演示视频](https://v.qq.com/x/page/y0621qszc78.html?)
https://v.qq.com/x/page/y0621qszc78.html

[牧师和魔鬼演示视频](https://v.qq.com/x/page/y06218hp78u.html?)
https://v.qq.com/x/page/y06218hp78u.html

本文已发表在CSDN博客
[Week4博客](https://blog.csdn.net/sysu997wang/article/details/79794260)
