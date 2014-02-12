using UnityEngine;
using System.Collections;

public class ObjectDestroyer : MonoBehaviour
{
    void OnTriggerEnter(Collider other)
    {
        Destroy(other.gameObject);
    }
}