using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Renderer))]
public class ShotBehaviour : MonoBehaviour
{
    public Color ShotColor = Color.red;
    //1 para o player e 2 para inimigos
    public int Type = 2;
    public float TimeoutInSeconds = 2;

    void Awake()
    {
    }

    void Start()
    {
        renderer.material.color = ShotColor;
        Destroy(gameObject, TimeoutInSeconds);
    }

    void Update()
    {
    }

    void OnCollisionEnter(Collision collision)
    {
        var layer = collision.gameObject.layer;
        //colisao com um asteroide
        if (layer == 9)
        {
            if (Type == 1)
            {
                GameController.Score += 5;
                GameController.createExplosion(rigidbody.position);
                Destroy(collision.gameObject);
            }
            Destroy(gameObject);
        }
        //colisão com uma nave
        else if (layer == 10)
        {
            if (Type == 1)
            {
                GameController.Score += 10;
                GameController.createExplosion(rigidbody.position);
                Destroy(collision.gameObject);
            }
            Destroy(gameObject);
        }
    }
}