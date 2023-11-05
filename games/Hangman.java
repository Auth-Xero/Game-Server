import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.types.GameRoom;
import com.katelyn_fred.game_server.server.types.GameUser;

import java.util.Random;

public class Hangman implements Game {
    private String name;
    private String description;
    private int id;
    private String wordToGuess;
    private String[] wordList = new String[]{"embassies","thereupon","malware","arteries","canned","lawrenceville","typekey","workmen","highbury","amstrad","boris","snorting","contestants","nicolas","servant","chien","rapp","religions","futon","roll","pursuing","thud","superman","marta","coverage","multisync","representing","rita","solenoid","problem","treatments","duval","prohibition","nunez","catalyst","housewives","tithe","arcadia","pains","fossils","rouse","gammon","morphy","northampton","surreal","couch","tide","staircase","heaton","unidentified","dalai","warns","negros","misheard","appalachia","photocopiers","beim","bromwich","windmill","tures","misspelled","tiesto","gretchen","willow","praia","inge","excursion","believed","haridwar","sateen","modality","telesales","gleeson","mickey","scalloped","brun","trooper","libido","chapman","preventive","legible","ensembles","skipped","photogallery","islet","weiter","comittment","server","diaz","worshipped","enrollee","swinging","ambiguity","gnostic","judas","solicitation","algiers","oppenheimer","gliding","movie","finalizing","oceana","oversee","boarding","madthumbs","bisexuales","circumstance","unified","dives","violation","kilmarnock","disposable","divisions","roared","preschool","deepak","parsing","podium","conservancy","martyrdom","preparedness","stale","woolly","taranaki","baruch","colleague","evolutionary","symbols","presumably","phenterminediscount","healers","dingle","stimulate","leningrad","izumi","erasing","mcnair","izzy","motility","agreeing","hendricks","monrovia","vessel","warrantless","etiquette","screenshot","marrow","rejection","insides","kerouac","vera","carpark","asymptotically","nntp","teeth","beleive","handmade","capsule","explaining","disturb","cucumbers","flaps","monotonic","compasses","novosibirsk","progresses","santa","encounter","visalia","extremists","reps","odom","trailers","splint","amber","programmer","snes","mcneil","barefoot","huntsville","mausoleum","quintet","bleached","bellamy","clamav","sills","connexion","wharfedale","phones","peas","grievance","dixon","freebies","perpetuate","intolerable","sportsman","canopies","additionally","overlay","harding","breaths","probe","grail","knack","mirc","dredging","cpsc","stopping","aktuelle","nominee","seahawks","chiefs","tern","distortions","recs","linger","monumental","sprint","peabody","gatineau","propositions","moyer","coed","versailles","fragment","sarees","journalistic","nutmeg","smarter","destruction","verdes","ultimately","wheeled","anonymous","roosevelt","taxes","movin","frontiers","aesthetically","scipy","rubs","hinds","supposedly","garantie","completes","correcting","fair","smith","alam","malignant","inverters","excalibur","defense","widget","pombe","initially","macau","pirated","sediment","muse","sundries","millenium","coney","snare","ayatollah","scart","kiosks","tinted","staphylococcus","basel","ltte","flet","exec","canfield","reverses","wrote","skylark","gardasee","admirably","obligor","tamworth","puddle","sustaining","deciduous","agenda","barcodes","eric","gorton","pura","predation","distorted","admit","packer","feet","culpepper","resultados","arbitration","clad","verilog","reusable"};
    private String currentWordState;
    private int maxAttempts;
    private int remainingAttempts;
    private GameUser[] players; // Store an array of GameUsers for multiple players.
    private int currentPlayerIndex; // Index of the current player's turn.
    private GameRoom gameRoom = null;
    private boolean started;

    public Hangman() {
        this.id = 1;
        this.name = "Hangman";
        this.description = "A word-guessing game where players try to discover the hidden word.";
        this.maxAttempts = 6; // You can customize the number of allowed attempts.
        started = false;
        initializeGame(getRequiredPlayerCount());
    }

    public Hangman(String dummy) {
        this.id = 1;
        this.name = "Hangman";
        this.description = "A word-guessing game where players try to discover the hidden word.";
        this.maxAttempts = 6; // You can customize the number of allowed attempts.
        started = false;
        //initializeGame(getRequiredPlayerCount());
    }

    public Hangman(int id, String name, String wordToGuess, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.wordToGuess = wordToGuess.toUpperCase(); // Convert the word to uppercase for consistency.
        this.maxAttempts = 6; // You can customize the number of allowed attempts.
        started = false;
        initializeGame(maxPlayers);
    }

    private void initializeGame(int maxPlayers) {
        // Initialize the array of players based on the maximum player count.
        players = new GameUser[maxPlayers];
        currentPlayerIndex = 0;
        Random random = new Random();

        int randomIndex = random.nextInt(wordList.length);

        this.wordToGuess = wordList[randomIndex].toUpperCase();
        // Initialize the current word state with underscores for each character.
        StringBuilder initialWordState = new StringBuilder();
        for (char c : wordToGuess.toCharArray()) {
            if (Character.isLetter(c)) {
                initialWordState.append('_');
            } else {
                initialWordState.append(c);
            }
        }
        this.currentWordState = initialWordState.toString();
        this.remainingAttempts = maxAttempts;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getRequiredPlayerCount() {
        return 2;
    }

    @Override
    public int getMaxPlayerCount() {
        return 2;
    }

    @Override
    public void start(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        started = true;
        initializePlayers(gameRoom);

        // Notify the first player about their turn and the game status.
        gameRoom.sendMessageToUser(players[currentPlayerIndex].getUsername(), name + " Game", "It's your turn!");
    }

    private void initializePlayers(GameRoom gameRoom) {
        for (int i = 0; i < players.length; i++) {
            players[i] = gameRoom.getGameUser(i);

            // Notify players about the game status.
            gameRoom.sendMessageToUser(players[i].getUsername(), name + " Game", "Waiting for other players...");
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public void playTurn(GameUser player, String data) {
        // Check if it's the player's turn and process their guess
        if (player == players[currentPlayerIndex]) {
            char guess = data.toUpperCase().charAt(0); // Convert the guess to uppercase.
            boolean guessCorrect = false;
            StringBuilder updatedWordState = new StringBuilder(currentWordState);

            for (int i = 0; i < wordToGuess.length(); i++) {
                if (wordToGuess.charAt(i) == guess) {
                    updatedWordState.setCharAt(i, guess);
                    guessCorrect = true;
                }
            }

            if (!guessCorrect) {
                remainingAttempts--;
            }

            currentWordState = updatedWordState.toString();

            if (currentWordState.equals(wordToGuess)) {
                // The player has guessed the word correctly.
                gameRoom.sendSystemMessageToAllPlayers(name + " Game", player.getUsername() + " wins! The word was: " + wordToGuess);
                // The player gets another turn.
                currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            } else if (remainingAttempts <= 0) {
                // No more attempts remaining; the game is over.
                gameRoom.sendSystemMessageToAllPlayers(name + " Game", "No attempts remaining. The word was: " + wordToGuess);
                gameRoom.sendGameOver();
            } else {
                // Notify all players about the game status and whose turn it is, showing correctly guessed letters.
                String wordWithSpaces = currentWordState.replaceAll("", " ").trim();
                gameRoom.sendSystemMessageToAllPlayers(name + " Game", "Word: " + wordWithSpaces + " | Attempts remaining: " + remainingAttempts);
                currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Move to the next player.
                gameRoom.sendMessageToUser(players[currentPlayerIndex].getUsername(), name + " Game", "It's your turn!");
            }
        } else {
            // Notify the player that it's not their turn
            gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Please wait for your turn.");
        }
    }

    @Override
    public boolean isGameOver() {
        return currentWordState.equals(wordToGuess) || remainingAttempts <= 0;
    }
}
