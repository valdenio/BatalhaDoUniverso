using UnityEngine;
using System.Collections;

public class WaitingScreen : MonoBehaviour
{
    private string Text = "Para jogar, conecte um dispositivo Android a este computador, através do nosso aplicativo disponível na Play Store.";
    private Rect rect;
    public float MenuHeight = 100;
    public float MenuWidth = 300;

    void Start()
    {
        rect = new Rect(Screen.width / 2 - MenuWidth / 2, Screen.height / 2 - MenuHeight / 2, MenuWidth, MenuHeight);
    }

    void Update()
    {
    }

    void OnGUI()
    {
        GUI.skin = GameController.customSkin;

        GUILayout.BeginArea(rect);
        GUILayout.Box(Text, GUILayout.Width(MenuWidth), GUILayout.Height(MenuHeight));
        GUILayout.EndArea();
    }
}