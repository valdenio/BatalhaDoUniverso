using UnityEngine;

[RequireComponent(typeof(Rigidbody))]
public class AccelerometerController : MonoBehaviour
{
    public GameObject Shot;
    public Transform ShotTrigger;
    public float ShotSpeed = 2000;
    public Color ShotColor = Color.yellow;
    public Vector3 StarshipSpeed = new Vector3(45, 25, 0);
    private Vector3 accelerometer;

    void Awake()
    {
    }

    void Start()
    {
        if (ShotTrigger == null)
        {
            ShotTrigger = this.transform;
        }
        GameController.Player = rigidbody;
        GameController.Health = 100;
        GameController.Score = 0;
    }

    void Update()
    {

    }

    void FixedUpdate()
    {
        shoot();
        move();
    }

    private void move()
    {
        float x = 0, y = 0, z = 0;
        accelerometer = GameController.Acceleration;
        x = accelerometer.x * StarshipSpeed.x;
        y = accelerometer.y * StarshipSpeed.y;
        rigidbody.AddForce(x, y, z);
    }

    private void shoot()
    {
        if (Input.GetKeyDown("tab"))
        {
            var shotInstance = Instantiate(Shot, ShotTrigger.position, Quaternion.Euler(0, 90, 90)) as GameObject;
            shotInstance.rigidbody.AddForce(ShotTrigger.right * (rigidbody.velocity.z + ShotSpeed));
            var script = shotInstance.GetComponent<ShotBehaviour>();
            script.ShotColor = ShotColor;
            script.Type = 1;

            if (ShotTrigger.collider)
            {
                Physics.IgnoreCollision(shotInstance.collider, ShotTrigger.collider);
            }
            Physics.IgnoreCollision(shotInstance.collider, gameObject.collider);
        }
    }

    void OnCollisionEnter(Collision collision)
    {
        rigidbody.velocity = Vector3.zero;
        var layer = collision.gameObject.layer;
        //colisao com as paredes invisiveis
        if (layer == 1)
        {
            //        rigidbody.velocity = Vector3.zero;
            //       rigidbody.isKinematic = true;
        }
        //colisao com asteroides
        else if (layer == 9)
        {
            GameController.Health = 0;
            Destroy(collision.gameObject);
            GameController.createExplosion(collision.rigidbody.position);
        }
        //se receber um tiro
        else if (layer == 8)
        {
            GameController.Health -= 10;
            Destroy(collision.gameObject);
        }
    }

    void OnCollisionExit(Collision collision)
    {
        //      rigidbody.isKinematic = false;
    }
}