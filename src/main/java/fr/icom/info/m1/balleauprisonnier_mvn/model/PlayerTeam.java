package fr.icom.info.m1.balleauprisonnier_mvn.model;

import java.util.ArrayList;
import java.util.Iterator;

import fr.icom.info.m1.balleauprisonnier_mvn.view.Field;

public class PlayerTeam
{
    // Les membres de l'équipe, un humain et des bots.
    private ArrayList<Bot> bot_players;
    private Human human_player;
    private String color;
    private String side;

    private Field field;

    // constructeur
    public PlayerTeam(int bot_nb, String color, String side)
    {
        this.color = color;
        this.side = side;

        this.bot_players = new ArrayList<Bot>();

        double width =  Field.getInstance().getWidth();
        double height =  Field.getInstance().getHeight();

        double y_init;
        double human_y_init;

        if(side == "top")
        {
            y_init = 20;
            human_y_init = 100;
        }
        else
        {
            y_init = height - 80;
            human_y_init = height - 160;
        }

        // On cree le joueur
        human_player = new Human(color, width/2, human_y_init, side);

        // On cree le nombre de bots indiqués
        for(int i = 0; i < bot_nb; i++)
        {
            double x_init = (i + 1) * width/(bot_nb+1);

            bot_players.add(new Bot(color, x_init, y_init,side));
        }
    }

    // Accesseurs
    public Human getHumanPlayer()
    {
        return human_player;
    }

    public String getSide()
    {
        return side;
    }

    // Methodes

    public ArrayList<Player> getPlayers()
    {
        ArrayList<Player> players = new ArrayList<Player>(bot_players);
        players.add(human_player);

        return players;
    }

    // Méthode qui vérifie si une balle lancée touche une des joueurs de l'équipe
    public void checkBallCollisions(Ball b)
    {
        // Cas spécial de mort du joueur humain : il remplace un des bots qui va mourir à sa place
        if(human_player.isTouched(b))
        {
            // On vérifie qu'il reste au moins un bot à remplacer
            if(bot_players.size() >= 1)
            {
                Bot bot_to_be_replaced = bot_players.get(0);

                // On place le joueur à la position du bot
                human_player.x = bot_to_be_replaced.x;
                human_player.y = bot_to_be_replaced.y;

                // On le retire de la liste des bots
                bot_players.remove(bot_to_be_replaced);
            }
            else
            {
                human_player = null;
            }
            // la vérification de fin de partie se fait plus tard dans la logique de jeu
        }

        // On utilise des iterateurs car on parcourt une liste dont on va (peut-être) retirer des éléments
        Iterator<Bot> it = bot_players.iterator();
        while (it.hasNext())
        {
            Bot next_bot = it.next();
            if(next_bot.isTouched(b))
            {
                // On retire l'iterateur
                // de cette manière, on peut continuer d'iterer sur la liste des bots sans erreur
                it.remove();
            }
        }
    }

    public boolean allPlayersKilled()
    {
        return (human_player == null && bot_players.size() == 0);
    }
}