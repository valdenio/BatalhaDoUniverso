using UnityEngine;
using System.Collections;

public class CreditsScreen : MonoBehaviour
{

    private string Text = "Jogo desenvolvido por Valdênio Paulino de Melo, sob orientação do professor Alexandre Garcia de Oliveira, para graduação no curso de Tecnologia em Jogos Digitais na FATEC São Caetano do Sul";
    private Rect rect;
    public float MenuHeight = 300;
    public float MenuWidth = 500;
    public GUISkin customSkin;

    void Start()
    {
        rect = new Rect(Screen.width / 2 - MenuWidth / 2, Screen.height / 2 - MenuHeight / 2, MenuWidth, MenuHeight);
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
