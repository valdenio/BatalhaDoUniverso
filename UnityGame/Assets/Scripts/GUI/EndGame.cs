using UnityEngine;
using System.Collections;

public class EndGame : MonoBehaviour
{
    public string Text = "";
    //salva o indice do elemento selecionado
    private int selectedIndex = 0;
    public int SelectedIndex
    {
        get { return selectedIndex; }
    }
    private Rect rect;
    private float MenuHeight;
    public float MenuWidth = 450;
    public int ButtonHeight = 60;
    //distancia entre os botoes
    public int SpaceBetweenButtons = 10;
    //texto dos botoes
    public string[] Buttons = { "Jogar Novamente", "Voltar ao Menu" };

    void Start()
    {
        //define a altura do menu
        MenuHeight = ((ButtonHeight + SpaceBetweenButtons) * (Buttons.Length + 1)) - SpaceBetweenButtons;
        //define a area do menu
        rect = new Rect(Screen.width / 2 - MenuWidth / 2, Screen.height / 2 - MenuHeight / 2, MenuWidth, MenuHeight);
    }

    void Update()
    {
        updateMenu();
    }

    void OnGUI()
    {
        GUI.skin = GameController.customSkin;

        GUILayout.BeginArea(rect);
        GUILayout.Box(Text, GUILayout.Width(MenuWidth), GUILayout.Height(ButtonHeight));
        for (int i = 0; i < Buttons.Length; i++)
        {
            GUI.SetNextControlName(Buttons[i]);
            GUILayout.Button(Buttons[i], GUILayout.Height(ButtonHeight));
            //adiciona um espaco entre os botoes, distribuindo proporcionalmente
            GUILayout.FlexibleSpace();
        }
        GUILayout.EndArea();
        GUI.FocusControl(Buttons[selectedIndex]);
    }

    private void updateMenu()
    {
        if (Input.GetKeyDown("up"))
        {
            selectedIndex--;
            if (selectedIndex < 0)
            {
                selectedIndex = Buttons.Length - 1;
            }
        }
        else if (Input.GetKeyDown("down"))
        {
            selectedIndex++;
            selectedIndex %= Buttons.Length;
        }
    }
}