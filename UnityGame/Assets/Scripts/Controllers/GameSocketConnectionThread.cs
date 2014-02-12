using Procurios.Public;
using System;
using System.Collections;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;

public class GameSocketConnectionThread : MonoBehaviour
{
    private const string host = "localhost";
    private const int port = 45;
    private Thread WorkerThread;
    private byte[] dataReceived = null;
    private string received = String.Empty;
    private static byte[] send = null;
    private static NetworkStream ns = null;
    private static TcpClient client;
    private static bool running = false;

    private string log = "";

    public void Start()
    {
        WorkerThread = new Thread(ThreadListener);
        WorkerThread.Start();
    }

    private void ThreadListener()
    {
        client = new TcpClient();
        try
        {
            client.Connect(host, port);
            ns = client.GetStream();

            write("READY");

            running = true;
            while (running)
            {
                threadUpdate();
            }
        }
        catch (SocketException e)
        {
            Debug.LogException(e, this);
            if (!running)
            {
                //tenta conectar novamente
                ThreadListener();
            }
            else
            {
                GameController.gameState = GameController.GameState.EXIT;
            }
        }
    }

    private void threadUpdate()
    {
        received = read().ToLower();
        switch (received)
        {
            case "waiting_android":
                GameController.gameState = GameController.GameState.WAITING;
                log = received;
                Debug.Log(log);
                break;
            case "game_ready":
                GameController.gameState = GameController.GameState.RESUME;
                log = received;
                Debug.Log(log);
                break;
            default:
                var table = JSON.JsonDecode(received) as Hashtable;
                if (table != null && table.ContainsKey("acelerometro"))
                {
                    var data = (Hashtable)table["acelerometro"];
                    var accelerometer = new Vector3(
                        Convert.ToSingle(data["y"]),
                        Convert.ToSingle(data["x"]),
                        Convert.ToSingle(data["z"])
                        );
                    // salva os dados do acelerometro
                    GameController.Acceleration = accelerometer;
                }
                break;
        };
    }

    public static void StopConnection()
    {
        running = false;
        if (ns != null)
        {
            ns.Close();
        }
        if (client != null)
        {
            client.Close();
        }
        //     WorkerThread.Join();
        //      WorkerThread = null;
    }

    public string read()
    {
        if (ns.CanRead)
        {
            dataReceived = new byte[1024];
            int length = ns.Read(dataReceived, 0, dataReceived.Length);
            return Encoding.Default.GetString(dataReceived, 0, length).Trim();
        }
        return String.Empty;
    }

    public static void write(string s)
    {
        if (ns != null && ns.CanWrite)
        {
            send = Encoding.Default.GetBytes(s);
            ns.Write(send, 0, send.Length);
        }
    }
}