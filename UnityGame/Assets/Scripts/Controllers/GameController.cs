using UnityEngine;

[RequireComponent(typeof(WaitingScreen))]
[RequireComponent(typeof(AlphaBackground))]
[RequireComponent(typeof(EndGame))]
[RequireComponent(typeof(GameHUD))]
[RequireComponent(typeof(AudioSource))]
[RequireComponent(typeof(MenuKeysController))]
[RequireComponent(typeof(GameSocketConnectionThread))]
public class GameController : MonoBehaviour
{
    public enum GameState { PAUSE, RESUME, EXIT, PLAY, FINISH, WAITING }

    private MenuKeysController pause;
    private WaitingScreen info;
    private GameHUD hud;
    private EndGame end;
    private AlphaBackground alpha;

    public GameObject Explosion;
    #region StaticVariables
    public static GameState gameState;

    public static GameObject explosionPrefab;

    // tempo limite do jogo
    public static float GameInterval;

    /// <summary>
    /// Armazena uma instância da nave do jogador
    /// </summary>
    public static Rigidbody Player { get; set; }

    /// <summary>
    /// Propriedade para armazenar os dados do acelerometro
    /// </summary>
    public static Vector3 Acceleration { get; set; }

    /// <summary>
    /// Propriedade que armazena a pontuação
    /// </summary>
    public static float Score { get; set; }

    private static float health;
    public GUISkin skin;
    public static GUISkin customSkin;

    /// <summary>
    /// Propriedade que armazena a vida do jogador
    /// </summary>
    public static float Health
    {
        get
        {
            return health;
        }
        set
        {
            if (value < health)
            {
                GameSocketConnectionThread.write("DAMAGE");
            }
            health = value;
        }
    }
    #endregion StaticVariables

    void Awake()
    {
        GameInterval = 60;
        Score = 0;
        gameState = GameState.WAITING;
        Acceleration = Vector3.zero;
        Time.timeScale = 0;
        customSkin = skin;
    }

    void Start()
    {
        explosionPrefab = Explosion;
        info = GetComponent<WaitingScreen>();
        info.enabled = false;
        pause = GetComponent<MenuKeysController>();
        pause.enabled = false;
        hud = GetComponent<GameHUD>();
        hud.enabled = false;
        end = GetComponent<EndGame>();
        end.enabled = false;
        alpha = GetComponent<AlphaBackground>();
        alpha.enabled = false;
    }

    void Update()
    {
        //verifica o fim do jogo
        if (GameInterval <= 0 || health <= 0)
        {
            Time.timeScale = 0;
            audio.Pause();
            alpha.enabled = true;
            end.enabled = true;
            if (Score <= 0)
            {
                end.Text = "Fim de jogo!\nVocê não conseguiu nenhum ponto.\n";
            }
            else
            {
                end.Text = "Fim de jogo!\nVocê conseguiu " + Score + " pontos!";
            }
            GameSocketConnectionThread.write("PAUSE");
            gameState = GameState.FINISH;
        }
        else
        {
            //atualiza o tempo do jogo
            GameInterval -= Time.deltaTime;
        }

        switch (gameState)
        {
            case GameState.EXIT:
                exitGame();
                Application.LoadLevel(0);
                break;

            case GameState.WAITING:
                showInfo();
                break;

            case GameState.PAUSE:
                showInfo();
                if (pause.enabled && Input.GetKeyDown("space"))
                {
                    checkMenu();
                }
                break;

            case GameState.RESUME:
                Time.timeScale = 1.0f;
                info.enabled = false;
                pause.enabled = false;
                alpha.enabled = false;
                hud.enabled = true;
                gameState = GameState.PLAY;
                audio.Play();
                break;

            case GameState.FINISH:
                alpha.enabled = true;
                pause.enabled = false;
                hud.enabled = false;
                if (Input.GetKeyDown("space"))
                {
                    checkEndMenu();
                }
                break;

            case GameState.PLAY:
                break;
        }

        if (gameState != GameState.WAITING && Input.GetKeyDown("escape"))
        {
            if (!pause.enabled)
            {
                pauseGame();
            }
            else
            {
                resumeGame();
            }
        }
    }

    private void checkMenu()
    {
        switch (pause.SelectedIndex)
        {
            case 0:
                resumeGame();
                break;
            case 1:
                pauseGame();
                Application.LoadLevel(0);
                break;
            default:
                break;
        }
    }

    private void checkEndMenu()
    {
        switch (end.SelectedIndex)
        {
            case 0:
                GameSocketConnectionThread.StopConnection();
                Application.LoadLevel(1);
                break;
            case 1:
                exitGame();
                Application.LoadLevel(0);
                break;
            default:
                break;
        }
    }

    public void resumeGame()
    {
        Time.timeScale = 1.0f;
        audio.Play();
        pause.enabled = false;
        GameSocketConnectionThread.write("PLAY");
        gameState = GameState.RESUME;
        alpha.enabled = false;
        hud.enabled = true;
    }

    public void pauseGame()
    {
        Time.timeScale = 0;
        audio.Pause();
        alpha.enabled = true;
        pause.enabled = true;
        GameSocketConnectionThread.write("PAUSE");
        gameState = GameState.PAUSE;
    }

    public void exitGame()
    {
        pause.enabled = false;
        end.enabled = false;
        info.enabled = false;
        GameSocketConnectionThread.write("PAUSE");
    }

    public void showInfo()
    {
        Time.timeScale = 0;
        audio.Pause();
        if (!pause.enabled)
        {
            alpha.enabled = true;
            info.enabled = true;
        }
    }

    void OnApplicationQuit()
    {
        GameSocketConnectionThread.write("STOP");
    }

    public static void createExplosion(Vector3 position)
    {
        Instantiate(explosionPrefab, position, Quaternion.identity);
    }
}