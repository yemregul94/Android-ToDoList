# Yapılacaklar Listesi (To Do List) Android Uygulaması

<a href="https://play.google.com/store/apps/details?id=com.moonlight.todolist" target="_blank">
<img src="http://pluspng.com/img-png/get-it-on-google-play-badge-png-open-2000.png" height="48"></a>

Uygulama, tamamen ücretsiz ve reklamsız bir yapılacaklar listesi uygulamasıdır. Kullanıcılar unutmak istemedikleri şeyleri not alabilir ve onları kolayca takip edebilirler. Firebase Realtime Database sayesinde, giriş yapılan herhangi bir cihazdan tüm verilere ulaşma imkanı sağlanıyor. Böylece senkron sorunu veya veri kaybı sorunu da ortadan kalkıyor. Sade ve zarif tasarımı sayesinde notlarınızı rahatlıkla görüntülenebilir ve eklenebilir.

Uygulama, internet tabanlı olsa da internetsiz de kullanılabiliyor. İnternetsiz kullanımda kaydedilen veriler internete bağlanınca direkt senkronize edilebiliyor. Uygulama internetsiz de çalışsa bile uzun süre internetsiz kullanmak tavsiye edilmiyor.

## Giriş Ekranları

Uygulama, Firebase Auth kullandığı için kullanıcıların email adresleri ile hesap açması ve bunlar ile kayıtlar oluşturması gerekiyor. Kullanıcılar Giriş, Kayıt ve Şifre Sıfırlama ekranlarında ilgili işlemleri yapabilmektedir. Bunun yanı sıra uygulamayı denemek isteyen kullanıcıların ilgisi kaçırmamak için uygulamada Misafir(anonim) hesap imkanı da vardır. Anonim hesaplar çıkış yapana kadar normal bir hesap gibi veri saklayabilirler ve istenirse email ve şifre girişi yapılarak gerçek hesaba dönüştürülebilirler.
Giriş ekranlarının öncesinde ise kısa bilgilendirme ekranları da uygulamanın ilk kurulumu sonrası kullanıcıları karşılıyor.

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Onboard.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Onboard.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Dark.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Dark.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Register.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Register.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Reset.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Reset.png" width="200" style="max-width:100%;"></a>

## Ana Ekran - Dashboard

Uygulamaya giriş yaptıktan sonra ilk yönlendirilen ekran olduğu için ana ekran görevi görüyor. Uygulamanın sonraki açılışlarında, eğer önceden giriş yapıldıysa giriş ekranı atlanıp direkt bu ekran açılıyor.

Bu ekranda, oluşturulmuş olan liste ögeleri görüntülenebiliyor ve üzerlerine tıklandığında da düzenleme ekranına yönlendiriliyor. Liste ögelerinin içinde yer alan alt görevlerin üzerine tıklayınca da onların tamamlanma işlemleri gerçekleşiyor. 
Sağ alttaki butona tıklandığında da yeni liste ögesi oluşturma ekranına yönlendirme yapılıyor. 

Üst menüde ise arama, filtreleme, sıralama, ayarlar/profil ve kategori filtreleme butonları yer alıyor. Bunları kullanarak aranan liste ögeleri rahatlıkla bulunabiliyor. Burada filtreler de birbirleriyle beraber çalışabiliyorlar.

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Dashboard_Dark.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Dashboard_Dark.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Dashboard_Light.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Dashboard_Light.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Filters.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Filters.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Sort.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Sort.png" width="200" style="max-width:100%;"></a>

Uygulamanın farklı yerlerinde olduğu gibi ana ekranda da kaydırma işlemleri destekleniyor. Sağa kaydırınca silme, sola kaydırınca tamamlama işlemleri yapılıyor.

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Swipe_Delete.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Swipe_Delete.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Swipe_Complete.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Swipe_Complete.png" width="200" style="max-width:100%;"></a>

## Detay Ekranı

Kullanıcılar, bu ekranda liste ögelerinin detaylarına ulaşıp onları düzenleyebiliyorlar. Yeni bir öge için ekran boş gelirken, mevcut ögeler için bilgiler ekrana işleniyor. 
Burada ögeleri ait başlık, açıklama, öncelik, kategori, renk ve alt görevler değiştirilebilirken tamamlama ve favori durumları da güncellenebiliyor. 
Bunun yanı sıra sayfadan ayrılmadan yeni kategori yaratmak veya ayarlar sayfasına ulaşmak da mümkün.

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/List_Details_Dark.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/List_Details_Dark.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/List_Details_Light.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/List_Details_Light.png" width="200" style="max-width:100%;"></a>

## Ayarlar

Bu ekranda kullanıcılar, hesaplarına dair ayarları yapabiliyorlar. Öncelikle eğer misafir/anonim hesap olarak giriş yaptıysalar burada o hesapları kayıtlı hesaba dönüştürebilirler. Kayıtlı hesaplar da email adresi ve şifre değişikliğini buradan yapabilirler.
Kullanıcılar dilerse kişisel verilerini korumak amacıyla verilerini ve hesaplarını da silebilirler. Liste ögelerinde kullanılan kategorileri de yine bu ekranda düzenleyebilirler.

Bunların yanı sıra uygulamanın görünümünü aydınlık/gündüz veya karanlık/gece modu arasında değiştirebilirler. Bu seçenek en başta sistem tercihini baz alsa da kullanıcılar diledikleri temayı seçebilirler.

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Settings.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Settings.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Save_Anon.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Save_Anon.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Settings_Login.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Settings_Login.png" width="200" style="max-width:100%;"></a>

## Dil ve Tema Destekleri

Önceki görsellerde göründüğü üzere uygulamanın gece ve gündüz modları bulunuyor. Bunun yanı sıra tüm değerler strings kaynak dosyasında olduğu için çoklu dil desteği de mevcut. Şu an için sadece ana dil olarak İngilizce ve çeviri olarak da Türkçe desteği mevcut.

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Light.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Light.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Dark.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Dark.png" width="200" style="max-width:100%;"></a>

<a href="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Dark_En.png" target="_blank">
<img src="https://github.com/yemregul94/Android-ToDoList/blob/main/screenshots/Login_Dark_En.png" width="200" style="max-width:100%;"></a>

## Kullanım videoları

Ana ekrandaki, detay ekranındaki ve ayarlar ekranındaki işlemleri aşağıdaki video ile daha rahat görebilirsiniz.

https://github.com/yemregul94/Android-ToDoList/assets/88839970/a1c24109-53dd-41d4-bc5b-ff5710f2fa09

Burada ise hesap ve anonim hesap işlemlerini görebilirsiniz.

https://github.com/yemregul94/Android-ToDoList/assets/88839970/973fa7d2-b4a7-46dc-aefa-543e13bc7a6b


## Kullanılan Teknolojiler ve Yapılar

- Kotlin & Android
- MVVM
- Firebase Auth
- Firebase Realtime Database
- Coroutines & Lifecycle
- Dagger & Hilt
- Data Binding
- View Binding
- Fragments
