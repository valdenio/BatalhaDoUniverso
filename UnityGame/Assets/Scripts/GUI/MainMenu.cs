using UnityEngine;
using System.Collections;

[RequireComponent(typeof(TutorialScreen))]
[RequireComponent(typeof(CreditsScreen))]
[RequireComponent(typeof(MenuKeysController))]
public class MainMenu : MonoBehaviour
{
    public Texture Background;
    private MenuKeysController menuController;
    public int LarguraTitulo = 500;
    public string Titulo = "BATALHA ESPACIAL";
    public GUIStyle EstiloTitulo;
    private Rect rect;
    private CreditsScreen credits;
    private TutorialScreen tutorial;

    void Start()
    {
        menuController = GetComponent<MenuKeysController>();
        credits = GetComponent<CreditsScreen>();
        credits.enabled = false;
        tutorial = GetComponent<TutorialScreen>();
        tutorial.enabled = false;

        rect = new Rect(Screen.width / 2 - LarguraTitulo / 2, 60, LarguraTitulo, 100);
    }

    void Update()
    {
        if (!tutorial.enabled && !credits.enabled)
        {
            menuController.enabled = true;
            if (Input.GetKeyDown("space") && menuController != null)
            {
                checkMenu();
            }
        }
    }

    private void checkMenu()
    {
        switch (menuController.SelectedIndex)
        {
            case 0:
                GameSocketConnectionThread.StopConnection();
                Application.LoadLevel(1);
                break;
            case 1:
                menuController.enabled = false;
                tutorial.enabled = true;
                credits.enabled = false;
                break;
            case 2:
                menuController.enabled = false;
                credits.enabled = true;
                tutorial.enabled = false;
                break;
            case 3:
                GameSocketConnectionThread.write("STOP");
                Application.Quit();
                break;
            default:
                break;
        }
    }

    void OnGUI()
    {
        GUI.depth = 2;
        if (Background)
        {
            GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), Background, ScaleMode.StretchToFill, true, 10.0F);
        }
        GUILayout.BeginArea(rect);
        GUILayout.Label(Titulo, EstiloTitulo);
        GUILayout.EndArea();
    }
}