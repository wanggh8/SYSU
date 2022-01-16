图片因不可知原因加载不出来，看完整版可前往[CSDN博客](https://blog.csdn.net/sysu997wang/article/details/79873001)
#  操作与总结
## 参考 Fantasy Skybox FREE 构建自己的游戏场景
#### 双摄像机使用： 用来俯瞰整个地图，位于右下角

![1](https://img-blog.csdn.net/20180409210752712?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 效果图

![2](https://img-blog.csdn.net/20180409210923908?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### SKY天空贴图，使用6张图贴成一个球状

![3](https://img-blog.csdn.net/20180409211100437?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

第一次贴图，效果还是不错的，主要是素材比较好

![4](https://img-blog.csdn.net/20180409211455823?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### 地形
比较高的五指山，和一些低的山脉。还有山中下路，以及平缓地区。种了很多树，还有草
![5](https://img-blog.csdn.net/20180409211644840?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![6](https://img-blog.csdn.net/20180409211849976?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

采用的素材
![7](https://img-blog.csdn.net/20180409211942381?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### 声音
采用了一个森林的音频文件，还是比较好听的，不足的是里面有鸟叫，我没做出来鸟。。。音频会在后面的演示链接里有。
![8](https://img-blog.csdn.net/20180409212118741?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### 第一人称视角

![9](https://img-blog.csdn.net/20180409212320728?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![10](https://img-blog.csdn.net/20180409212402455?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

游戏结果

![11](https://img-blog.csdn.net/20180409212547196?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)


##  写一个简单的总结，总结游戏对象的使用
游戏对象是所有出现在场景中的实体，他们可以直接创建模型，也可以通过脚本，预设动态创建游戏对象。对于游戏对象，可以进行很多操作，以及添加其他组件。比如常用的transform对象，主要负责控制对象的位置，方向和大小。通过对游戏对象的操作，实现了游戏的一些基本操作。游戏对象还有一些负责场景和控制的，它们通常负责着对游戏规则和场景地图。

# 编程实践
牧师与魔鬼 动作分离版
上周实现了牧师和魔鬼的普通版，这周进行牧师与魔鬼动作分离版的设计与开发。
一共分成8个C#代码文件，其中CCActionManager和FirstSceneController挂载在一个空对象上，View挂载在摄像机上

![12](https://img-blog.csdn.net/20180410171217919?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### SSAction
动作基类,同时声明返回信息的接口。主要是动作的基本属性
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public enum SSActionEventType : int { Started, Competeted }

public interface ISSActionCallback
{
    void SSActionEvent(SSAction source, SSActionEventType events = SSActionEventType.Competeted,
        int intParam = 0, string strParam = null, Object objectParam = null);
}
public class SSAction : ScriptableObject {
    public bool enable = true;
    public bool destory = false;
    // Use this for initialization
    public GameObject gameObject { get; set; }
    public Transform transform { get; set; }
    public ISSActionCallback callback { get; set; }
    protected SSAction() {}
    public virtual void Start () {
        throw new System.NotImplementedException();
    }
	
	// Update is called once per frame
	public virtual void Update () {
        throw new System.NotImplementedException();
    }
}
```
#### CCMovePeople
涉及牧师和魔鬼的上下船动作
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CCMovePeople : SSAction {
    public FirstSceneController sceneController;
    public static CCMovePeople GetSSAction()
    {
        CCMovePeople action = ScriptableObject.CreateInstance<CCMovePeople>();
        return action;
    }
    // Use this for initialization
    public override void Start () {
        sceneController = (FirstSceneController)SSDirector.getInstance().currentScenceController;
        int num = 0;
        if (gameObject.name == "0")
        {
            num = 0;
        }
        if (gameObject.name == "1")
        {
            num = 1;
        }
        if (gameObject.name == "2")
        {
            num = 2;
        }
        if (gameObject.name == "3")
        {
            num = 3;
        }
        if (gameObject.name == "4")
        {
            num = 4;
        }
        if (gameObject.name == "5")
        {
            num = 5;
        }
        
        if (sceneController.leftSide[num] != null)
        {

            if (sceneController.leftSide[6] != null)
            {
                if (sceneController.boat[0] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = -1 - num;
                    SSDirector.getInstance().move1 = -6;
                }
                else if (sceneController.boat[1] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = -1 - num;
                    SSDirector.getInstance().move1 = -7;
                }

            }
        }
        else if (sceneController.rightSide[num] != null)
        {
            if (sceneController.rightSide[6] != null)
            {
				
                if (sceneController.boat[0] == null)
                {
                    
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = num;
                    SSDirector.getInstance().move1 = 6;
                    
                }
                
                else if (sceneController.boat[1] == null)
                {
                    SSDirector.getInstance().state = State.getOn;
                    SSDirector.getInstance().move = num;
                    SSDirector.getInstance().move1 = 7;
                }

            }
        }
        
        if (sceneController.leftSide[6] != null && sceneController.boat[0] != null && sceneController.boat[0].name == gameObject.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = -6;
            SSDirector.getInstance().move1 = -1 - num;
        }
        if (sceneController.leftSide[6] != null && sceneController.boat[1] != null && sceneController.boat[1].name == gameObject.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = -7;
            SSDirector.getInstance().move1 = -1 - num;
        }
        if (sceneController.rightSide[6] != null && sceneController.boat[0] != null && sceneController.boat[0].name == gameObject.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = 6;
            SSDirector.getInstance().move1 = num;
        }
        if (sceneController.rightSide[6] != null && sceneController.boat[1] != null && sceneController.boat[1].name == gameObject.name)
        {
            SSDirector.getInstance().state = State.getOff;
            SSDirector.getInstance().move = 7;
            SSDirector.getInstance().move1 = num;

        }
        Debug.Log("10");
	}

    // Update is called once per frame
    public override void Update () {
        if (SSDirector.getInstance().state == State.getOn)
        {
            Debug.Log("12");
            int a = SSDirector.getInstance().move;
            int b = SSDirector.getInstance().move1;
            if (a >= 0)
            {
                sceneController.rightSide[a].transform.position = Vector3.MoveTowards(sceneController.rightSide[a].transform.position, new Vector3(b - 4, -1.4f, 0), Time.deltaTime * 5);
                if (sceneController.rightSide[a].transform.position == new Vector3(b - 4, -1.4f, 0))
                {
                    sceneController.boat[b - 6] = sceneController.rightSide[a];
                    sceneController.rightSide[a] = null;
                    SSDirector.getInstance().state = State.still;
                }
            }
            if (a < 0)
            {
                Debug.Log("WWW");
                sceneController.leftSide[-a - 1].transform.position = Vector3.MoveTowards(sceneController.leftSide[-a - 1].transform.position, new Vector3(-b - 9, -1.4f, 0), Time.deltaTime * 5);
                if (sceneController.leftSide[-a - 1].transform.position == new Vector3(-b - 9, -1.4f, 0))
                {
                    sceneController.boat[-b - 6] = sceneController.leftSide[-a - 1];
                    sceneController.leftSide[-a - 1] = null;
                    SSDirector.getInstance().state = State.still;
                }
            }

        }
        if (SSDirector.getInstance().state == State.getOff)
        {
            Debug.Log("13");
            int a = SSDirector.getInstance().move;
            int b = SSDirector.getInstance().move1;
            if (b >= 0)
            {
                sceneController.boat[a - 6].transform.position = Vector3.MoveTowards(sceneController.boat[a - 6].transform.position, new Vector3(4.5f + 1f * b, -0.9f, 0), Time.deltaTime * 5);
                if (sceneController.boat[a - 6].transform.position == new Vector3(4.5f + 1f * b, -0.9f, 0))
                {
                    sceneController.rightSide[b] = sceneController.boat[a - 6];
                    sceneController.boat[a - 6] = null;
                    SSDirector.getInstance().state = State.still;
                    sceneController.Judge();
                    this.destory = true;
                    this.callback.SSActionEvent(this);
                }
            }
            if (b < 0)
            {
                sceneController.boat[-a - 6].transform.position = Vector3.MoveTowards(sceneController.boat[-a - 6].transform.position, new Vector3(-3.5f + 1f * b, -0.9f, 0), Time.deltaTime * 5);
                if (sceneController.boat[-a - 6].transform.position == new Vector3(-3.5f + 1f * b, -0.9f, 0))
                {
                    sceneController.leftSide[-b - 1] = sceneController.boat[-a - 6];
                    sceneController.boat[-a - 6] = null;
                    SSDirector.getInstance().state = State.still;
                    sceneController.Judge();
                    this.destory = true;
                    this.callback.SSActionEvent(this);
                }
            }

        }
	}
}
```
#### MoveBoat
涉及船的移动和船上的牧师和魔鬼的移动
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CCMoveBoat : SSAction {
    public FirstSceneController sceneController;
    // Use this for initialization
    public static CCMoveBoat GetSSAction()
    {
        CCMoveBoat action = ScriptableObject.CreateInstance<CCMoveBoat> ();
        return action;
    }
    public override void Start () {
        sceneController = (FirstSceneController)SSDirector.getInstance().currentScenceController;
        if (sceneController.boat[0] != null || sceneController.boat[1] != null)
        {
            if (sceneController.leftSide[6] != null)
            {
                sceneController.leftSide[6] = null;
                SSDirector.getInstance().state = State.toRight;
            }
            if (sceneController.rightSide[6] != null)
            {
                sceneController.rightSide[6] = null;
                SSDirector.getInstance().state = State.toLeft;
            }
            if (sceneController.leftSide[6] == null && sceneController.rightSide[6] == null)
            {
                Debug.Log("船正在行驶");
            }
        }
    }

    // Update is called once per frame
    public override void Update(){

        
        if (SSDirector.getInstance().state == State.toRight)
        {
            if (sceneController.boat[0] != null)
            {
                sceneController.boat[0].transform.position = Vector3.MoveTowards(sceneController.boat[0].transform.position, new Vector3(2, -1.4f, 0), Time.deltaTime * 5);
            }
            if (sceneController.boat[1] != null)
            {
                sceneController.boat[1].transform.position = Vector3.MoveTowards(sceneController.boat[1].transform.position, new Vector3(3, -1.4f, 0), Time.deltaTime * 5);
            }
            sceneController.boat_obj.transform.position = Vector3.MoveTowards(sceneController.boat_obj.transform.position, sceneController.BoatRightPos, Time.deltaTime * 5);
            if (sceneController.boat_obj.transform.position == sceneController.BoatRightPos)
            {
                sceneController.rightSide[6] =sceneController.boat_obj;
                SSDirector.getInstance().state = State.still;
                sceneController.Judge();
                this.destory = true;
                this.callback.SSActionEvent(this);
            }
    
        }
        if (SSDirector.getInstance().state == State.toLeft)
        {
            if (sceneController.boat[0] != null)
            {
                sceneController.boat[0].transform.position = Vector3.MoveTowards(sceneController.boat[0].transform.position, new Vector3(-3, -1.4f, 0), Time.deltaTime * 5);
            }
            if (sceneController.boat[1] != null)
            {
                sceneController.boat[1].transform.position = Vector3.MoveTowards(sceneController.boat[1].transform.position, new Vector3(-2, -1.4f, 0), Time.deltaTime * 5);
            }
            sceneController.boat_obj.transform.position = Vector3.MoveTowards(sceneController.boat_obj.transform.position, sceneController.BoatLeftPos, Time.deltaTime * 5);
            if (sceneController.boat_obj.transform.position == sceneController.BoatLeftPos)
            {
                sceneController.leftSide[6] = sceneController.boat_obj;
                SSDirector.getInstance().state = State.still;
                sceneController.Judge();
                this.destory = true;
                this.callback.SSActionEvent(this);
            }
 
        }
	}
}
```
#### SSActionManager
动作管理基类，创建 管理一个动作集合，动作做完自动回收动作。
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SSActionManager : MonoBehaviour {
    private Dictionary<int, SSAction> actions = new Dictionary<int, SSAction>();
    private List<SSAction> waitingAdd = new List<SSAction>();
    private List<int> waitingDelete = new List<int>();
 
	protected void Update () {
		foreach(SSAction ac in waitingAdd) {
            actions[ac.GetInstanceID()] = ac;
        }
        waitingAdd.Clear();
		foreach(KeyValuePair <int, SSAction> kv in actions) {
            SSAction ac = kv.Value;
			if (ac.destory) {
                waitingDelete.Add(ac.GetInstanceID());
            }
			else if (ac.enable) {
                ac.Update();
            }
        }
		foreach (int key in waitingDelete) {
            SSAction ac = actions[key];
			actions.Remove(key);
            DestroyObject(ac);
        }
        waitingDelete.Clear();
    }
    public void RunAction(GameObject gameobject, SSAction action, ISSActionCallback manager)
    {
        action.gameObject = gameobject;
        action.transform = gameobject.transform;
        action.callback = manager;
        waitingAdd.Add(action);
        action.Start();
    }
    void Start()
    {

    }
}
```
#### CCActionManager
实战动作管理,负责接收场景控制的命令，管理动作的自动执行
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CCActionManager : SSActionManager, ISSActionCallback{
	public FirstSceneController sceneController;
	public CCMoveBoat MoveBoat;
    public CCMovePeople MovePeople;
    // Use this for initialization
    void Start () {
        sceneController = (FirstSceneController)SSDirector.getInstance().currentScenceController;
        sceneController.actionManager = this;
        
	}
	
	// Update is called once per frame
	void Update () {
        base.Update();
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
            MovePeople = CCMovePeople.GetSSAction();
            this.RunAction(gameObj, MovePeople, this);
            Debug.Log("1");
        }
        else if (gameObj.name == "boat")
        {
            Debug.Log("4");
            MoveBoat = CCMoveBoat.GetSSAction();
            this.RunAction(gameObj, MoveBoat, this);
        }
    }
    public void SSActionEvent(SSAction source, SSActionEventType events = SSActionEventType.Competeted,
        int intParam = 0, string strParam = null, Object objectParam = null)
    {
        
    }
}
```
#### FirstSceneController
场景控制,负责游戏场景和对象的加载以及游戏规则的实行。
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
    public SSActionManager actionManager { get; set; }
    public Vector3 LeftSidePos = new Vector3(-7, -3.5f, 0);
    public Vector3 RightSidePos = new Vector3(7, -3.5f, 0);
    //岸边位置
    public Vector3 BoatLeftPos = new Vector3(-2.45f, -3, 0);
    public Vector3 BoatRightPos = new Vector3(2.45f, -3, 0);
    //船在两岸的起始位置
    public GameObject[] leftSide = new GameObject[7];
    public GameObject[] rightSide = new GameObject[7];

    //两岸和岸边的对象
    public GameObject boat_obj, leftSide_obj, rightSide_obj;
    public GameObject[] boat = new GameObject[2];//船上的对象

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
        Judge();
        
    }
}
```
#### SSDirector
导演，负责控制、协调整个游戏的运行。
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
#### View
UI设计，以及用户操作
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public interface UserAction
{

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
## 实验结果
程序能完美运行，同时设置了时间60秒的限制，能够使用户在上下船和移动船进行的操作无效。设置重新开始按钮，能够在失败后快速重新开始。

### 演示视频

[Fantasy Skybox（https://v.qq.com/x/page/r0626erlx3j.html?）](https://v.qq.com/x/page/r0626erlx3j.html?)

[牧师与魔鬼动作分离版（https://v.qq.com/x/page/p0626tzb8lv.html?）](https://v.qq.com/x/page/p0626tzb8lv.html?)

本文已发表在CSDN博客 [CSDN博客](https://blog.csdn.net/sysu997wang/article/details/79873001)


