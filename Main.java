import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

// Record untuk menyimpan data Tamu
record Tamu(String nama, String idTamu) {}

// Record untuk menyimpan data Kamar
record Kamar(String nomorKamar, String tipeKamar, double harga) {}

// Class untuk mengelola Hotel
class Hotel {
    private final List<Kamar> daftarKamar = new ArrayList<>();
    private final Set<Tamu> tamuSet = new HashSet<>();
    private final Map<String, Kamar> kamarMap = new HashMap<>();
    private final ConcurrentMap<String, Tamu> tamuMap = new ConcurrentHashMap<>();
    private final Deque<String> antrianPemesanan = new ConcurrentLinkedDeque<>();
    private final Map<String, String> reservasiMap = new HashMap<>(); // Menyimpan reservasi kamar

    // Immutable Set (Fasilitas Hotel)
    private final Set<String> fasilitasHotel = Set.of("Kolam Renang", "Gym", "Restoran", "WiFi Gratis");

    // Immutable Map (Harga berdasarkan tipe kamar)
    private final Map<String, Double> hargaTipeKamar = Map.of(
        "Deluxe", 500000.0,
        "Standard", 300000.0,
        "Suite", 800000.0,
        "Family", 600000.0,
        "Presidential", 1200000.0
    );

    // Menambahkan kamar
    public void tambahKamar(Kamar kamar) {
        daftarKamar.add(kamar);
        kamarMap.put(kamar.nomorKamar(), kamar);
    }

    // Menambahkan tamu
    public void tambahTamu(Tamu tamu) {
        tamuSet.add(tamu);
        tamuMap.put(tamu.idTamu(), tamu);
    }

    // Memesan kamar
    public void pesanKamar(String nomorKamar, String idTamu) {
        if (reservasiMap.containsKey(nomorKamar)) {
            System.out.println("Kamar " + nomorKamar + " sudah dipesan.");
            return;
        }

        Optional<Kamar> kamarOptional = Optional.ofNullable(kamarMap.get(nomorKamar));
        Optional<Tamu> tamuOptional = Optional.ofNullable(tamuMap.get(idTamu));

        if (kamarOptional.isPresent() && tamuOptional.isPresent()) {
            reservasiMap.put(nomorKamar, idTamu);
            antrianPemesanan.addLast("Tamu " + tamuOptional.get().nama() + " memesan kamar: " + kamarOptional.get().nomorKamar());
            System.out.println("Reservasi berhasil!");
        } else {
            System.out.println("Kamar atau Tamu tidak ditemukan.");
        }
    }

    // Memesan kamar dengan prioritas (ditambahkan ke depan antrian)
    public void pesanKamarDenganPrioritas(String nomorKamar, String idTamu) {
        Optional<Kamar> kamarOptional = Optional.ofNullable(kamarMap.get(nomorKamar));
        Optional<Tamu> tamuOptional = Optional.ofNullable(tamuMap.get(idTamu));

        if (kamarOptional.isPresent() && tamuOptional.isPresent()) {
            antrianPemesanan.addFirst("PRIORITAS: Tamu " + tamuOptional.get().nama() + " memesan kamar: " + kamarOptional.get().nomorKamar());
            System.out.println("Reservasi prioritas berhasil!");
        } else {
            System.out.println("Kamar atau Tamu tidak ditemukan.");
        }
    }

    // Memproses pemesanan dari belakang
    public void prosesPemesananDariBelakang() {
        if (!antrianPemesanan.isEmpty()) {
            System.out.println("Memproses (dari belakang): " + antrianPemesanan.pollLast());
        } else {
            System.out.println("Tidak ada pemesanan dalam antrian.");
        }
    }

    // Membatalkan reservasi
    public void batalkanReservasi(String nomorKamar, String idTamu) {
        if (reservasiMap.containsKey(nomorKamar) && reservasiMap.get(nomorKamar).equals(idTamu)) {
            reservasiMap.remove(nomorKamar);
            System.out.println("Reservasi kamar " + nomorKamar + " oleh tamu ID " + idTamu + " telah dibatalkan.");
        } else {
            System.out.println("Reservasi tidak ditemukan atau data tidak sesuai.");
        }
    }

    // Menampilkan daftar reservasi
    public void tampilkanReservasi() {
        System.out.println("Daftar Reservasi:");
        if (reservasiMap.isEmpty()) {
            System.out.println("Tidak ada reservasi saat ini.");
        } else {
            reservasiMap.forEach((kamar, tamu) -> 
                System.out.println("Kamar " + kamar + " dipesan oleh Tamu ID: " + tamu));
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hotel hotel = new Hotel();

        // Menambahkan kamar
        hotel.tambahKamar(new Kamar("101", "Deluxe", 500000));
        hotel.tambahKamar(new Kamar("102", "Standard", 300000));
        hotel.tambahKamar(new Kamar("103", "Suite", 800000));

        // Menambahkan tamu
        hotel.tambahTamu(new Tamu("Alice", "T001"));
        hotel.tambahTamu(new Tamu("Bob", "T002"));

        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Lakukan reservasi");
            System.out.println("2. Lakukan reservasi prioritas");
            System.out.println("3. Proses pemesanan dari belakang");
            System.out.println("4. Lihat daftar reservasi");
            System.out.println("5. Keluar");
            System.out.print("Pilihan: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            switch (pilihan) {
                case 1:
                    System.out.print("Masukkan nomor kamar: ");
                    String nomorKamar = scanner.nextLine();
                    System.out.print("Masukkan ID tamu: ");
                    String idTamu = scanner.nextLine();
                    hotel.pesanKamar(nomorKamar, idTamu);
                    break;
                case 2:
                    System.out.print("Masukkan nomor kamar: ");
                    String nomorPrioritas = scanner.nextLine();
                    System.out.print("Masukkan ID tamu: ");
                    String idPrioritas = scanner.nextLine();
                    hotel.pesanKamarDenganPrioritas(nomorPrioritas, idPrioritas);
                    break;
                case 3:
                    hotel.prosesPemesananDariBelakang();
                    break;
                case 4:
                    hotel.tampilkanReservasi();
                    break;
                case 5:
                    running = false;
                    System.out.println("Terima kasih!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
        scanner.close();
    }
}
