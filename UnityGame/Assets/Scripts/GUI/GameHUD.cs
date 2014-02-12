using UnityEngine;
using System.Collections;

public class GameHUD : MonoBehaviour
{

    void OnGUI()
    {
        GUI.skin = GameController.customSkin;

        var vida = GameController.Health;
        if (vida < 0)
        {
            vida = 0;
        }
        GUI.Label(new Rect(20, 20, 160, 50), "VIDA: " + vida);

        var tempo = Mathf.FloorToInt(GameController.GameInterval);
        if (tempo < 0)
        {
            tempo = 0;
        }
        GUI.Label(new Rect(Screen.width / 2 - 80, 20, 100, 50), tempo.ToString());
        GUI.Label(new Rect(Screen.width - 220, 20, 180, 50), "PONTOS: " + GameController.Score);
    }
}
