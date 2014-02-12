using UnityEngine;
using System.Collections;

public class RigidbodySpawner : MonoBehaviour
{
    public Rigidbody Prefab;
    public float Speed = 1000;
    public Vector3 Rotation = new Vector3(0, 0, 0);
    public float SpawnRate = 5;
    private float nextSpawn;
    private Vector3 spawnPosition;

    void Start()
    {
        nextSpawn = Time.time;
    }

    void Update()
    {
        if (Time.time > nextSpawn)
        {
            spawn();
            nextSpawn = Time.time + SpawnRate;
        }
    }

    private void spawn()
    {
        spawnPosition = new Vector3(
            Random.Range(collider.bounds.min.x, collider.bounds.max.x),
            Random.Range(collider.bounds.min.y, collider.bounds.max.y),
            collider.bounds.max.z
            );

        var obj = Instantiate(Prefab, spawnPosition, Quaternion.Euler(Rotation)) as Rigidbody;
        obj.AddForce(Vector3.back * Speed);
        Physics.IgnoreCollision(obj.collider, gameObject.collider);
    }
}