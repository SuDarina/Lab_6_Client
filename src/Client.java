import Commands.Comands;
import Exceptions.FieldException;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    String comand1 = "";
    SocketChannel channel;
    int i = 0;

    public void setClient() throws  FieldException, InterruptedException {
        try {
            i++;
            channel = SocketChannel.open(new InetSocketAddress("localhost", 3545));
            i = 0;
            System.out.println("Connected to server");
            Scanner scr = new Scanner(System.in);
            String comand = "";
            ByteBuffer bb = ByteBuffer.allocate(3000);


            boolean isWorking = true;
            while (isWorking) {
                String[] ss;
                Comands comands = new Comands();
                if (!(comand1.equals("execute_script")))
                    comand = scr.nextLine();

                if (comand.contains(" ")) {
                    ss = comand.split(" ");
                    comands.setName(ss[0]);
                    comands.setArgs(ss[1]);
                } else {
                    comands.setName(comand);
                    System.out.println("имя: " + comand);
                }
                if (comands.getName().equals("add") | comands.getName().equals("add_if_min") | comands.getName().equals("remove_greater") | comands.getName().equals("remove_lower") | comands.getName().equals("update_by_id")) {
                    FormWorker fw = new FormWorker();
                    fw.add_name();
                    fw.add_coordinates();
                    fw.add_x();
                    fw.add_y();
                    fw.add_salary();
                    fw.add_position();
                    fw.add_status();
                    fw.add_height();
                    fw.add_passportID();
                    fw.add_person();
                    if (comands.getName().equals("update_by_id")) {
                        fw.worker.setId(Integer.parseInt((String) comands.getArgs()));
                        //System.out.println(Integer.parseInt((String) comands.getArgs()));
                    }
                    comands.setArgs(fw.worker);
                }
                if (comands.getName().equals("count_less_than_start_date")) {
                    FormDate fd = new FormDate();
                    fd.countDate();
                    fd.countTime();
                    fd.formDateTime();
                    comands.setArgs(fd.ldt);
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                if (!(comand1.equals("execute_script"))) {

                    oos.writeObject(comands);
                    bb.flip();
                    bb.clear();

                    bb.put(baos.toByteArray());
                    bb.flip();
                    channel.write(bb);
                    if (bb.hasRemaining()) {
                        bb.compact();
                    } else {
                        bb.clear();
                    }
                }


                channel.read(bb);
                bb.flip();
                String serverAnswer = new String(bb.array(), bb.position(), bb.remaining());
                System.out.println(serverAnswer);
                bb.clear();

                if (serverAnswer.equals("[программа завершена]"))
                    System.exit(0);


                if (serverAnswer.equals("[введите worker'а]")) {
                    FormWorker fww = new FormWorker();
                    fww.add_name();
                    fww.add_coordinates();
                    fww.add_x();
                    fww.add_y();
                    fww.add_salary();
                    fww.add_position();
                    fww.add_status();
                    fww.add_height();
                    fww.add_passportID();
                    fww.add_person();
                    comands.setArgs(fww.worker);
                    oos.writeObject(comands);
                    bb.flip();
                    bb.clear();

                    bb.put(baos.toByteArray());
                    bb.flip();
                    channel.write(bb);
                    if (bb.hasRemaining()) {
                        bb.compact();
                    } else {
                        bb.clear();
                    }
                }

                if (serverAnswer.equals("[введите дату и время]")) {

                    FormDate fdd = new FormDate();
                    fdd.countDate();
                    fdd.countTime();
                    fdd.formDateTime();
                    comands.setArgs(fdd.ldt);
                    oos.writeObject(comands);
                    bb.flip();
                    bb.clear();

                    bb.put(baos.toByteArray());
                    bb.flip();
                    channel.write(bb);
                    if (bb.hasRemaining()) {
                        bb.compact();
                    } else {
                        bb.clear();
                    }
                }


                if (serverAnswer.equals("[выполнение скрипта закончено]")) {
                    comand1 = "";
                    continue;
                }

                if (comands.getName().equals("execute_script"))
                    comand1 = "execute_script";

                baos.close();
                oos.close();

            }



        } catch (ConnectException e) {
            if (i == 1)
                System.out.println("[сервер временно недоступен]");
            Thread.sleep(2000);
            setClient();

        } catch (IOException e) {
            setClient();
        } catch(StackOverflowError e){
            System.out.println("Время на отладку вышло, попробуйте перезапустить приложение позже");
        }
    }
}
