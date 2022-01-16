
# 第一次作业
## 简答题
### 1.解释 游戏对象（GameObjects） 和 资源（Assets）的区别与联系。
#### 区别：
游戏对象是出现在场景中的所有物体。 即游戏中的每个对象都是一个游戏对象。游戏对象自己不做任何事。他们需要专有属性，才可以成为一个角色，一个环境，或一个特殊效果。游戏对象是一种容器。
资源是项目中的素材，包括图像、视频、脚本文件、预制文件等。

#### 联系： 
游戏对象可保存为资源，资源可以作为组件或整个游戏对象创建游戏对象实例。
### 2.下载几个游戏案例，分别总结资源、对象组织的结构（指资源的目录组织结构与游戏对象树的层次结构）
#### 资源的目录组织结构
资源一般根据一些标准进行分类，然后放入到不同的文件夹。通过文件夹的形式来实现资源的目录组织结构
如图
![资源的目录](https://img-blog.csdn.net/20180326132334729?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

将音频单独放入Audio文件夹，游戏人物放入Sprites文件夹，将帮助信息放入到TutorialInfo文件夹，预设放入Prefabs。同时把游戏的核心放入到_Complete-Game文件夹下，code放入到Scripts，场景放入到Scenes等。

![code](https://img-blog.csdn.net/20180326132945290?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

#### 游戏对象树的层次结构
游戏对象树类似于树，一个游戏对象往往是包括了多个子对象。子对象又包括了它的子对象
如图

![游戏对象树](https://img-blog.csdn.net/20180326133421883?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### 3.编写一个代码，使用 debug 语句来验证 MonoBehaviour 基本行为或事件触发的条件
#### a.基本行为包括 Awake() ，Start()， Update()， FixedUpdate() ，LateUpdate()
#### b.常用事件包括 OnGUI() ，OnDisable()， OnEnable()
```using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class behavior : MonoBehaviour {

    // Use this for initialization
    void Start()
    {
        Debug.Log("Start");
    }

    private void Awake()
    {
        Debug.Log("Awake");
    }

    void Update()
    {
        Debug.Log("Update");
    }

    private void FixedUpdate()
    {
        Debug.Log("FixedUpdate");
    }

    private void LateUpdate()
    {
        Debug.Log("LateUpdate");
    }

    private void OnGUI()
    {
        Debug.Log("OnGUI");
    }

    private void OnDisable()
    {
        Debug.Log("OnDisable");
    }

    private void OnEnable()
    {
        Debug.Log("OnEnable");
    }
}
```
实验结果

![结果](https://img-blog.csdn.net/20180326172737862?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

Awake 
当前控制脚本实例被装载的时候调用。一般用于初始化整个实例使用。
 
Start 
当前控制脚本第一次执行Update之前调用。
 
Update
每帧都执行一次。这是最常用的事件函数。

FixedUpdate 
 每固定帧绘制时执行一次。
 
LateUpdate 
在每帧执行完毕调用，他是在所有update结束后才掉，比较适合用于命令脚本的执行。

OnDisable  
 当对象变为不可用或非激活状态时此函数被调用。当物体被销毁时它将被调用，并且可用于任意清理代码。当脚本编译完成之后被重加载时，OnDisable将被调用。
 
OnEnable 
当对象变为可用或激活状态时此函数被调用。

OnGUI 
绘制GUI时候触发。一般在这个函数里绘制GUI菜单

### 4.查找脚本手册，了解 GameObject，Transform，Component 对象
#### a.分别翻译官方对三个对象的描述（Description）
GameObject是unity中代表人物角色、道具和风景的基本对象。它们本身没有做太多的实现，但是它们是实现了真正功能的组件的容器。

Transform对象的位置，旋转和缩放。
场景中的每个对象都有一个Transform。它用于存储和操纵对象的位置，旋转和缩放。每个变换都可以有一个父元素，它允许您分层应用位置，旋转和缩放。这是在“层次结构”窗格中显示的层次结构。他们还支持枚举器，因此您可以使用以下方式循环访问子代

component是所有附加到GameObjects的基类。并且，代码永远不会直接创建组件。 而是编写脚本代码，并将脚本附加到GameObject。
#### b.描述下图中 table 对象（实体）的属性、table 的 Transform 的属性、 table 的部件
**本题目要求是把可视化图形编程界面与 Unity API 对应起来，当你在 Inspector 面板上每一个内容，应该知道对应 API。**
**例如：table 的对象是 GameObject，第一个选择框是 activeSelf 属性。**

![图](https://pmlpml.github.io/unity3d-learning/images/ch02/ch02-homework.png)

##### table对象属性
table的对象是GameObject
layer（图层，默认值）、scene（场景）、tag（标签，untagged）、飞非静态的
##### table 的 Transform 的属性
位置在(0,0,0)
旋转参数(0,0,0)
大小在初始值1
##### table 的部件
Tranform、Cube(Mesh Filter)、Box Collider、Mesh Renderer
#### c.用 UML 图描述 三者的关系（请使用 UMLet 14.1.1 stand-alone版本出图）

![uml](https://img-blog.csdn.net/20180326181711169?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### 5.整理相关学习资料，编写简单代码验证以下技术的实现：
#### a.查找对象
```
public static GameObject Find(string name); 
//通过名字查找
public static GameObject FindWithTag(string tag);
//通过标签查找单个对象
public static GameObject[] FindGameObjectsWithTag(string tag);
//通过标签查找多个对象
```
#### b.添加子对象
```
public static GameObect CreatePrimitive(PrimitiveTypetype);
```
#### c.遍历对象树
```
foreach (Transform child in transform) {  
    Debug.Log(child.gameObject.name);  
} 
```
#### d.清除所有子对象
```
foreach (Transform child in transform) {  
    Destroy(child.gameObject);  
} 
```
### 6.资源预设（Prefabs）与 对象克隆 (clone)
#### a.预设（Prefabs）有什么好处？
预设像一个类模板，可以迅速方便创建大量相同属性的对象、操作简单，代码量少，减少出错概率。修改的复杂度降低，一旦需要修改所有相同属性的对象，只需要修改预设即可，所有通过预设实例化的对象都会做出相应变化。
#### b.预设与对象克隆 (clone or copy or Instantiate of Unity Object) 关系？
修改预设后，所有通过预设实例化的对象都会做出相应变化。但是克隆只是复制一个一模一样的对象，这个对象独立于原来的对象，在修改的过程中不会影响原有的。
#### c.制作 table 预制，写一段代码将 table 预制资源实例化成游戏对象
```
public GameObject table;
 void Start () {
        Debug.Log("Init Start");
        GameObject newTable = (GameObject)Instantiate(table.gameObject);
}
```
### 7.尝试解释组合模式（Composite Pattern / 一种设计模式）。使用 BroadcastMessage() 方法向子对象发送消息·
组合模式，将对象组合成树形结构以表示“部分-整体”的层次结构，组合模式使得用户对单个对象和组合对象的使用具有一致性。组合模式可以让客户端像修改配置文件一样简单的完成本来需要流程控制语句来完成的功能。
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public class behavior : MonoBehaviour
{

    // Use this for initialization
    void Start()
    {
        Debug.Log("Start");
        this.BroadcastMessage("testBroad", "hello sons!");
    }
    public void testBroad(string str)
    {
        print("son1 received: " + str);
    }

    private void Awake()
    {
        Debug.Log("Awake");
    }

    void Update()
    {
        Debug.Log("Update");
    }

    private void FixedUpdate()
    {
        Debug.Log("FixedUpdate");
    }

    private void LateUpdate()
    {
        Debug.Log("LateUpdate");
    }

    private void OnGUI()
    {
        Debug.Log("OnGUI");
    }

    private void OnDisable()
    {
        Debug.Log("OnDisable");
    }

    private void OnEnable()
    {
        Debug.Log("OnEnable");
    }
}
```
控制台输出结果

![结果](https://img-blog.csdn.net/20180326194757242?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N5c3U5OTd3YW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## 编程实践
### 代码实现
```
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class game : MonoBehaviour
{

    private int[] array;
    private int turn;
    //存储玩家
    private string result;
    private int res;
    //存储结果
    void Start()
    {
        reset();
    }
    void reset()//初始化
    {
        array = new int[9];
        turn = 1;
        res = 0;
        for (int i = 0; i < 9; i++)
        {
            array[i] = 0;
        }
    }
    // Update is called once per frame
    void Update()//判断结果
    {
        if (res == 0)
        {
            result = "";
        }
        if (res == 1)
        {
            result = "play1 win!!!";
        }
        if (res == -1)
        {
            result = "play2 win!!!";
        }
        if (res == 2)
        {
            result = "dead game!";
        }
    }
    void LateUpdate()
    {
        res = isWin();
    }

    void OnGUI()//UI实现
    {
        GUI.BeginGroup(new Rect(Screen.width / 2 - 150, Screen.height / 2 - 150, 300, 300));
        GUI.Box(new Rect(0, 0, 300, 300), icon1);
        if (res != 0)
        {
            GUIStyle style = GUI.skin.GetStyle("label");
            style.normal.textColor = new Color(1, 0, 0);
            style.fontSize = 42;
            GUI.Label(new Rect(40, 50, 220, 200), result);

            if (GUI.Button(new Rect(90, 190, 120, 40), "restart game"))
            {
                reset();
            }
        }
        else
        {
            for (int i = 0; i <= 2; i = i + 1)
            {
                for (int t = 0; t <= 2; t++)
                {
                    if (array[i * 3 + t] == 0)
                    {
                        if (GUI.Button(new Rect(20 + t * 90, i * 90, 80, 80), ""))
                        {
                            array[i * 3 + t] = turn;
                            turn = 0 - turn;
                        }
                    }
                    if (array[i * 3 + t] == 1)
                    {
                        GUI.Button(new Rect(20 + t * 90, i * 90, 80, 80), "X");
                    }
                    if (array[i * 3 + t] == -1)
                    {
                        GUI.Button(new Rect(20 + t * 90, i * 90, 80, 80), "O");
                    }
                }
            }
        }

        GUI.EndGroup();
    }
    private int isWin()
    {
        for (int i = 0; i < 3; i++)
        {
            if (array[0 + i * 3] == 1 && array[1 + i * 3] == 1 && array[2 + i * 3] == 1)
            {
                return 1;
            }
            if (array[0 + i * 3] == -1 && array[1 + i * 3] == -1 && array[2 + i * 3] == -1)
            {
                return -1;
            }
            if (array[0 + i] == 1 && array[3 + i] == 1 && array[6 + i] == 1)
            {
                return 1;
            }
            if (array[0 + i] == -1 && array[3 + i] == -1 && array[6 + i] == -1)
            {
                return -1;
            }
        }
        if (array[0] == 1 && array[4] == 1 && array[8] == 1)
        {
            return 1;
        }
        if (array[0] == -1 && array[4] == -1 && array[8] == -1)
        {
            return -1;
        }
        if (array[2] == 1 && array[4] == 1 && array[6] == 1)
        {
            return 1;
        }
        if (array[2] == -1 && array[4] == -1 && array[6] == -1)
        {
            return -1;
        }
        for (int i = 0; i < 9; i++)
        {
            if (array[i] == 0)
            {
                return 0;
            }
        }
        return 2;
    }
}

```
### 演示视频

https://pan.baidu.com/s/1mw_zM5PC6OBg-9rj6SUafA

视频超过了25m，上传到了百度网盘
此外本文发表到csdn博客
地址 https://blog.csdn.net/sysu997wang/article/details/79696637
