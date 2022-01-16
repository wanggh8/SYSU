# 题目
改进飞碟（Hit UFO）游戏：
游戏内容要求：
按 adapter模式 设计图修改飞碟游戏
使它同时支持物理运动与运动学（变换）运动

![2](https://img-blog.csdn.net/20180424204340112?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

# 游戏规则

一共有3个回合，每个回合有20个飞盘。飞盘的大小和移动速度会随着回合而变化，整体难度增加。在每回合中飞盘的颜色不同，击中蓝色飞盘得一分，击中黄色飞盘得两分，击中红色飞盘得三分，每回合分别得够30分、60分、90分，才能进入下个回合。在20个飞盘出现完后，如果不够要求分数则游戏失败。游戏失败后可以重新开始游戏。

# 程序组件

代码文件

![1](https://img-blog.csdn.net/20180424202833950?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

预制文件

![2](https://img-blog.csdn.net/20180417230304681?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

# 程序代码

SSDirector
导演类，负责控制、协调整个游戏的运行。
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public enum State
{
    win, fail, start, still,wait
}

public class SSDirector : System.Object

{
    private static SSDirector _instance;
    public State state { get; set; }
    public ISceneController currentScenceController { get; set; }
    public bool running { get; set; }

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
}
```
SceneController
场景控制,负责游戏场景和对象的加载以及游戏规则的实行.
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
public interface ISceneController
{
    void LoadResources();
}

public class SceneController : MonoBehaviour, ISceneController, IUserAction
{
    public SSActionManager actionManager { get; set; }
    public int round = 0;//轮数
    public int score = 0;//分数
    public Text Score;
    public Text Round;
    public Text Text;
    public Text Lost;
    public int num = 0;
    public int lost = 0;
 
    GameObject explosion;
    GameObject plane;

    public void LoadResources()
    {
        explosion = Instantiate(Resources.Load("prefabs/Explosion"), new Vector3(0, 0, -20), Quaternion.identity) as GameObject;
        plane = Instantiate(Resources.Load("prefabs/Plane"), new Vector3(0, 0, 2), Quaternion.identity) as GameObject;
    }
    void Awake()
    //创建导演实例并载入资源
    {
        SSDirector director = SSDirector.getInstance();
        director.state = State.wait;
        DiskFactory DF = DiskFactory.getInstance();
        DF.sceneControler = this;
        director.currentScenceController = this;
        director.currentScenceController.LoadResources();
        director.setFPS(60);
    }
    void Start()
    {
        round = 1;
    }

    // Update is called once per frame
    void Update()
    {
        lost = num - score;
        Score.text = "Score:" + score.ToString();
        Round.text = "Round:" + round.ToString();
        Lost.text = "Lost:" + lost.ToString();
        if (Input.GetMouseButtonDown(0) && SSDirector.getInstance().state == State.start)
        {
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit;
            if (Physics.Raycast(ray, out hit))
            {
                if (hit.transform.tag == "Disk")
                {
                    explosion.transform.position = hit.collider.gameObject.transform.position;
                    explosion.GetComponent<Renderer>().material = hit.collider.gameObject.GetComponent<Renderer>().material;
                    explosion.GetComponent<ParticleSystem>().Play();
                    hit.collider.gameObject.SetActive(false);
                    score += 1;
                }
            }
        }
        if (SSDirector.getInstance().state == State.fail)
        {
            Text.text = "Game Over!";
            SSDirector.getInstance().state = State.fail;
        }
        if (SSDirector.getInstance().state == State.win)
        {
            Text.text = "You Win!";
            SSDirector.getInstance().state = State.wait;
        }
    }
   
    public void StartGame()
    {
        num = 0;
        if (SSDirector.getInstance().state == State.wait)
        {
            SSDirector.getInstance().state = State.start;//进入倒计时状态
    
        }
    }
    public void ReStart()
    {
        if (SSDirector.getInstance().state == State.fail) {
            SSDirector.getInstance().state = State.wait;
            SceneManager.LoadScene("task1");
            Awake();
            Start();
        }
            
    }

}

```
SSAction
动作基类，模板
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SSAction : ScriptableObject
{
    public bool enable = true;
    public bool destroy = false;

    public GameObject gameobject { get; set; }
    public Transform transform { get; set; }

    protected SSAction() { }

    public virtual void Start()
    {
        throw new System.NotImplementedException();
    }

    public virtual void Update()
    {
        throw new System.NotImplementedException();
    }
}
```
Move
飞盘移动的类，控制飞盘的动作
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Move : SSAction
{
    public SceneController sceneControler = (SceneController)SSDirector.getInstance().currentScenceController;
    public GameObject end;   //要到达的目标  
    public float speed;    //速度  
    float startX;
    float endX;
    private float distanceToEnd;   
    
 

    public override void Start()
    {
        speed = 4 + sceneControler.round * 3;//速度随轮数变化
        startX = 5 - Random.value * 10;//在（-5,5）随机发射
        if (Random.value > 0.5)//使其对角发射
        {
         endX = 30;
        }
        else
        {
         endX = -30;
        }
        this.transform.position = new Vector3(startX, 0, -5);
        end = new GameObject();
        end.transform.position = new Vector3 (endX, -5, 30);
        //计算两者之间的距离  
        distanceToEnd = Vector3.Distance(this.transform.position, end.transform.position);
    }
    public static Move GetSSAction()
    {
        Move action = ScriptableObject.CreateInstance<Move>();
        return action;
    }
    public override void Update()
    {
        Vector3 endPos = end.transform.position;
        gameobject.transform.LookAt (endPos);

        //计算弧线中的夹角 
        float angle = Mathf.Min(1, Vector3.Distance(gameobject.transform.position, endPos) / distanceToEnd) * 45;
        gameobject.transform.rotation = gameobject.transform.rotation * Quaternion.Euler(Mathf.Clamp(-angle, -42, 42), 0, 0);
        float currentDist = Vector3.Distance(gameobject.transform.position, end.transform.position);
        gameobject.transform.Translate(Vector3.forward * Mathf.Min(speed * Time.deltaTime, currentDist));
        if (this.transform.position == end.transform.position)
        {
            DiskFactory.getInstance().cacheDisk(gameobject);
            Destroy (end);
            this.enable = false;
            this.destroy = true;
        }
    }
}
```
CCActionManager
动作管理基类
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CCActionManager : SSActionManager
{
    public SceneController sceneController;
    public DiskFactory diskFactory;
    public Move DiskMove;
    int count = 0;
    protected void Start()
    {
        sceneController = (SceneController)SSDirector.getInstance().currentScenceController;
        diskFactory = DiskFactory.getInstance();
        sceneController.actionManager = this;
    }

    // Update is called once per frame
    protected new void Update()
    {
        if (sceneController.num <= 20 &&sceneController.round <= 3 && SSDirector.getInstance().state == State.start)
        {
            count++;
            if (count == 60)
            {
                DiskMove = Move.GetSSAction();
                this.RunAction(diskFactory.getDisk(sceneController.round), DiskMove);
                sceneController.num++;
                count = 0;
            }
            base.Update();
        }
    }
}

```
SSActionManager
动作管理基类，创建 管理一个动作集合，动作做完自动回收动作
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SSActionManager : MonoBehaviour
{
    private Dictionary<int, SSAction> actions = new Dictionary<int, SSAction>();
    private List<SSAction> waitingAdd = new List<SSAction>();
    private List<int> waitingDelete = new List<int>();

    // Use this for initialization
    void Start()
    {

    }

    // Update is called once per frame
    protected void Update()
    {
        foreach (SSAction ac in waitingAdd)
        {
            actions[ac.GetInstanceID()] = ac;
        }
        waitingAdd.Clear();

        foreach (KeyValuePair<int, SSAction> kv in actions)
        {
            SSAction ac = kv.Value;
            if (ac.destroy)
            {
                waitingDelete.Add(ac.GetInstanceID());
            }
            else if (ac.enable)
            {
                ac.Update();
            }
        }

        foreach (int key in waitingDelete)
        {
            SSAction ac = actions[key];
            actions.Remove(key);
            DestroyObject(ac);
        }
        waitingDelete.Clear();
    }

    public void RunAction(GameObject gameobject, SSAction action)
    {
        action.gameobject = gameobject;
        action.transform = gameobject.transform;
        waitingAdd.Add(action);
        action.Start();
    }
}
```
PhysicActionManager
物理动作管理
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PhysicActionManager : SSActionManager
{

    /** 
 * 这个文件是管理动作的具体动作管理器，是用来管理飞碟的飞行动作 
 */
    public SceneController sceneController;
    public DiskFactory diskFactory;
    public Move DiskMove;
    int count = 0;
    protected void Start()
    {
        sceneController = (SceneController)SSDirector.getInstance().currentScenceController;
        diskFactory = DiskFactory.getInstance();
        sceneController.actionManager = this;
    }

    // Update is called once per frame
    protected new void Update()
    {
        if (sceneController.num <= 20 && sceneController.round <= 3 && SSDirector.getInstance().state == State.start)
        {
            count++;
            if (count == 60)
            {
                DiskMove = Move.GetSSAction();
                this.RunAction(diskFactory.getDisk(sceneController.round), DiskMove);
                sceneController.num++;
                count = 0;
            }
            base.Update();
        }
    }
}
```
Singleton
单例模式模板
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Singleton<T> : MonoBehaviour where T : MonoBehaviour
{

    protected static T instance;

    public static T Instance
    {
        get
        {
            if (instance == null)
            {
                instance = (T)FindObjectOfType(typeof(T));
                if (instance == null)
                {
                    Debug.LogError("An instance of " + typeof(T)
                        + " is needed in the scene, but there is none.");
                }
            }
            return instance;
        }
    }
}
```
DiskFactory
工厂类，控制飞盘的创建与销毁
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public class DiskFactory : System.Object
{
    private static DiskFactory _instance;
    public SceneController sceneControler { get; set; }
    public List<GameObject> used;
    public List<GameObject> cache;
    // Use this for initialization

    public static DiskFactory getInstance()
    {
        if (_instance == null)
        {
            _instance = new DiskFactory();
            _instance.used = new List<GameObject>();
            _instance.cache = new List<GameObject>();
        }
        return _instance;
    }

    public GameObject getDisk(int round)
    {
        if (sceneControler.score >= round * 30)
        //每轮总共发射20个，如果得分达到一定要求进入下一轮，否则GameOver
        {
            sceneControler.round++;
            sceneControler.num = 0;
        }
        if (sceneControler.num >= 20)
        {
            SSDirector.getInstance().state = State.fail;//游戏结束
        }
        if (sceneControler.round == 4)
        {
            SSDirector.getInstance().state = State.win;
        }
        GameObject newDisk;
        if (cache.Count == 0)
        {
            newDisk = GameObject.Instantiate(Resources.Load("prefabs/Disk"), new Vector3(0, 0, -10), Quaternion.identity) as GameObject;
        }
        else
        {
            newDisk = cache[0];
            cache.Remove(cache[0]);
        }
        switch (round)
        {
            case 1:
                float size1 = 1.0f;
                newDisk.transform.localScale = new Vector3(size1, size1 / 3, size1);
                newDisk.AddComponent<DiskData>();
                float a = Random.value;
                if (a < 0.33)//bule
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.blue;
                    newDisk.GetComponent<DiskData>().scores = 1;
                }
                else if (a > 0.33 && a < 0.66)
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.yellow;
                    newDisk.GetComponent<DiskData>().scores = 2;
                }
                else
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.red;
                    newDisk.GetComponent<DiskData>().scores = 3;
                }
                break;
            case 2:
                float size2 = 0.8f;
                newDisk.transform.localScale = new Vector3(size2, size2 / 3, size2);
                float b = Random.value;
                if (b < 0.33)//bule
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.blue;
                }
                else if (b > 0.33 && b < 0.66)
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.yellow;
                }
                else
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.red;
                }
                break;
            case 3:
                float size3 = 0.6f;
                newDisk.transform.localScale = new Vector3(size3, size3 / 3, size3);
                float c = Random.value;
                if (c < 0.33)//bule
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.blue;
                }
                else if (c > 0.33 && c < 0.66)
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.yellow;
                }
                else
                {
                    newDisk.GetComponent<Renderer>().material.color = Color.red;
                }
                break;
        }
        used.Add(newDisk);
        return newDisk;
    }

    public void cacheDisk(GameObject disk1)
    {
        for (int i = 0; i < used.Count; i++)
        {
            if (used[i] == disk1)
            {
                used.Remove(disk1);
                disk1.SetActive(true);
                cache.Add(disk1);
            }
        }
    }
}

```
UserGUI
用户界面，与用户交互
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
public interface IUserAction
{
    void StartGame();
    void ReStart();
}

public class UserGUI : MonoBehaviour
{
    private IUserAction action;
    // Use this for initialization
    void Start()
    {
        action = SSDirector.getInstance().currentScenceController as IUserAction;
    }
    void OnGUI()
    {
        GUIStyle fontstyle1 = new GUIStyle();
        fontstyle1.fontSize = 50;
        fontstyle1.normal.textColor = new Color(255, 255, 255);
        if (SSDirector.getInstance().state == State.wait)
        {
            if (GUI.Button(new Rect(0, 20, 120, 40), "开始游戏"))
            {
                action.StartGame();
            }
        }
        if (SSDirector.getInstance().state == State.fail)
            {    
            if (GUI.Button(new Rect(0, 20, 120, 40), "重新开始"))
            {
                action.ReStart();
            }
        }
        
    }
    // Update is called once per frame
    void Update()
    {
        //
    }
}
```
# 游戏演示

[演示视频](https://v.qq.com/x/page/i0637jsx9a2.html)

