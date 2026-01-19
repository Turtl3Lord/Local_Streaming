interface HeaderProps {
  title: string;
}

export function Header({ title }: HeaderProps) {
  return (
    <header className="sticky top-0 z-10 bg-[#F9FAFB]/80 backdrop-blur-md px-8 py-4">
      <h1 className="text-xl font-semibold text-gray-900 capitalize">
        {title}
      </h1>
    </header>
  );
}
