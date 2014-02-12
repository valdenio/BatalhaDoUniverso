using UnityEngine;
using System.Collections;

public class TutorialScreen : MonoBehaviour
{

    private string Text = "Você deve conseguir o maior número de pontos possível antes que o tempo acabe!\n\nOs pontos podem ser obtidos destruindo as naves e asteroides que aparecem na sua frente.\n\n" +
        " - Cada nave vale 10 pontos e cada asteróide vale 5 pontos;\n - Se você levar um tiro, perde 10 pontos de vida;\n" + 
        " - Se colidir com uma nave, perde 20 pontos de vida;\n - Se a sua nave colidir com um asteróide, você perde o jogo!\n" + 
        "\nPara controlar a nave, você deve ter o nosso aplicativo instalado em seu <i>Android</i>. Nós utilizamos os movimentos do seu celular para movimentar a nave!\n\n";

    private Rect rect;
    public float MenuHeight = 400;
    public float MenuWidth = 700;
    public GUISkin customSkin;

    void Start()
    {
        rect = new Rect(Screen.width / 2 - MenuWidth / 2, 120, MenuWidth, MenuHeight);
    }

    void Update()
    {
        if (Input.GetKeyDown("escape"))
        {
            enabled = false;
        }
    }

    void OnGUI()
    {
        GUI.depth = 0;
        GUI.skin = customSkin;

        GUI.Box(new Rect(20, Screen.height - 60, 750, 40), "Pressione 'Voltar' no seu <i>Android</i> ou ESC para retornar ao menu");
        GUILayout.BeginArea(rect);
        GUILayout.Box(Text, GUILayout.Width(MenuWidth), GUILayout.Height(MenuHeight));
        GUILayout.EndArea();
    }
}