using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Rigidbody))]
public class EnemyController : MonoBehaviour
{
    public GameObject Shot;
    public Transform ShotTrigger;
    public float ShotSpeed = 2000;
    private float nextShot;
    public float ShotRate = 3;
    public Vector3 ShotRotation = new Vector3(0, -90, 90);
    public float Dano = 20;

    void Awake()
    {
    }

    void Start()
    {
        if (ShotTrigger == null)
        {
            ShotTrigger = this.transform;
        }
        nextShot = Time.time;
    }

    void Update()
    {
        if (Time.time > nextShot)
        {
            shoot();
            nextShot = Time.time + ShotRate;
        }
    }

    private void shoot()
    {
        var shotInstance = Instantiate(Shot, ShotTrigger.position, Quaternion.Euler(ShotRotation)) as GameObject;
        shotInstance.rigidbody.AddForce(ShotTrigger.right * (rigidbody.velocity.z + ShotSpeed));

        if (ShotTrigger.collider)
        {
            Physics.IgnoreCollision(shotInstance.collider, ShotTrigger.collider);
        }
        Physics.IgnoreCollision(shotInstance.collider, gameObject.collider);
    }

    void OnCollisionEnter(Collision collision)
    {
        //colisao com o jogador
        if (collision.gameObject.tag.Equals("Player", System.StringComparison.OrdinalIgnoreCase))
        {
            GameController.Health -= Dano;
            GameController.createExplosion(rigidbody.position);
            Destroy(gameObject);
        }
    }
}