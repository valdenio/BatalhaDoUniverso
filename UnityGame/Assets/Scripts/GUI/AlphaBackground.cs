using UnityEngine;
using System.Collections;

public class AlphaBackground : MonoBehaviour
{
    public Texture Background;

    void OnGUI()
    {
        GUI.depth = 2;
        if (Background)
        {
            GUI.DrawTexture(new Rect(20, 20, Screen.width - 40, Screen.height - 40), Background, ScaleMode.StretchToFill);
        }
    }
}
